package com.haywaa.ups.cooperate.event.impl;

import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alibaba.fastjson.JSON;
import com.haywaa.ups.cooperate.event.EventTimeline;
import com.haywaa.ups.cooperate.event.entity.Event;
import com.haywaa.ups.cooperate.event.entity.LocalEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-28 14:13
 */
public abstract class AbstractEventTimeline implements EventTimeline, InitializingBean, ApplicationContextAware, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractEventTimeline.class);

    private static final int PAGE_SIZE = 20;

    private ApplicationContext applicationContext;

    private long currentStamp;

    private ThreadPoolExecutor executor;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // init stamp, 获取当前timeline最大stamp
        currentStamp = this.initStamp();

        executor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(), r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("event-catch-thread");
            return thread;
        });
        executor.execute(this);
    }

    @Override
    public void run() {
        // 监听时间线中的事件，并发布本地事件通知业务方
        while (true) {
            try {
                if (applicationContext == null) {
                    // 等待初始化完成，再开始处理
                    Thread.sleep(2000);
                    continue;
                }

                boolean currentStampChanged = false;
                while (true) {
                    PageResult pageResult = fetchNextEvent(currentStamp, PAGE_SIZE);
                    if (pageResult == null) {
                        break;
                    }

                    List<Event> eventList = pageResult.list;
                    if (eventList == null || eventList.isEmpty()) {
                        break;
                    }

                    for (Event event : eventList) {
                        try {
                            applicationContext.publishEvent(new LocalEvent(event));
                        } catch (Exception e) {
                            logger.error("handle event [{}] error => {}", JSON.toJSONString(event), e.getMessage(), e);
                        }
                    }

                    currentStamp = pageResult.nextStamp;
                    currentStampChanged = true;
                    Thread.sleep(20);
                }

                if (currentStampChanged) {
                    markCurrentStamp(currentStamp);
                }
                Thread.sleep(2000);
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                } else {
                    logger.error("event listener error => {}", e.getMessage(), e);
                }
            }
        }
    }

    protected abstract long initStamp();

    /**
     * 不含nextStamp对应事件（>）
     */
    protected abstract PageResult fetchNextEvent(long nextStamp, int pageSize);

    /**
     * 标记当前节点stamp偏移量，供clean做参考
     */
    protected abstract void markCurrentStamp(long stamp);

    protected abstract void clean();

    @AllArgsConstructor
    @Getter
    static class PageResult {

        long nextStamp;

        List<Event> list;
    }
}
