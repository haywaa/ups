package com.haywaa.ups.cooperate.event.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import com.alibaba.fastjson.JSON;
import com.haywaa.ups.cooperate.event.entity.Event;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-27 18:13
 */
//@Component
public class RedisEventTimeline extends AbstractEventTimeline {

    private static final String REDIS_KEY = "ups_event_timeline";

    private DefaultRedisScript<Integer> scriptAddEvent;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        // init script
        initScript();
        // init stamp, 获取当前timeline最大stamp
        super.afterPropertiesSet();
    }

    private void initScript() {
        scriptAddEvent = new DefaultRedisScript<>();
        scriptAddEvent.setScriptText(
                "-- stamp = 获取当前最大score + 1 （不要完全clean队列必须保证至少有一个）\n" +
                "local score = redis.call('zrange', KEYS[1], -1, -1, 'WITHSCORES')\n" +
                "local stamp = 1\n" +
                "if score ~= false and #score ~= 0 then\n" +
                "    -- score下标1开始\n" +
                "    stamp = score[2] + 1\n" +
                "end\n" +
                "redis.call('zadd', KEYS[1], stamp, KEYS[2])\n" +
                "return 1"
        );
    }

    /**
     * 获取Timeline最后一个元素的score作为stamp
     */
    @Override
    protected long initStamp() {
        Set<ZSetOperations.TypedTuple<String>> result = stringRedisTemplate.opsForZSet().rangeWithScores(REDIS_KEY, -1, -1);
        if (result == null || result.isEmpty()) {
            return 0L;
        }

        for (ZSetOperations.TypedTuple<String> tuple : result) {
            if (tuple.getScore() != null) {
                return tuple.getScore().longValue();
            }
        }

        return 0L;
    }

    @Override
    public void pushEvent(Event event) {
        Integer result = stringRedisTemplate.execute(scriptAddEvent, Arrays.asList(REDIS_KEY, JSON.toJSONString(event)), null);
        if (result != 1) {
            // TODO
        }
    }

    @Override
    public void pushEvent(List<Event> event) {
        //TODO batch
        //Integer result = stringRedisTemplate.execute(scriptAddEvent, Arrays.asList(REDIS_KEY, JSON.toJSONString(event)), null);
        //if (result != 1) {
        //    // TODO
        //}
    }

    @Override
    protected PageResult fetchNextEvent(long nextStamp, int pageSize) {
        return null;
    }

    @Override
    protected void markCurrentStamp(long stamp) {

    }

    @Override
    protected void clean() {

    }
}
