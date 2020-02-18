package com.haywaa.ups.event.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.haywaa.ups.dao.EventTimelineDAO;
import com.haywaa.ups.domain.entity.EventTimelineDO;
import com.haywaa.ups.event.entity.Event;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-28 14:12
 */
@Component
public class MysqlEventTimeline extends AbstractEventTimeline {

    @Autowired
    private EventTimelineDAO eventTimelineDAO;

    @Override
    protected long initStamp() {
        Long stamp = eventTimelineDAO.selectMaxId();
        return stamp == null ? 0L : stamp;
    }

    @Override
    public void pushEvent(Event event) {
        EventTimelineDO timelineDO = new EventTimelineDO();
        timelineDO.setEventType(event.getEventType());
        timelineDO.setEventCode(event.getEventCode());
        timelineDO.setVal(event.getVal());
        timelineDO.setVal1(event.getVal1());
        eventTimelineDAO.insert(timelineDO);
    }

    @Override
    protected PageResult fetchNextEvent(long nextStamp, int pageSize) {
        List<EventTimelineDO> list = eventTimelineDAO.selectNextPage(nextStamp, pageSize);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        List<Event> eventList = new ArrayList<>(list.size());
        for (EventTimelineDO timelineDO : list) {
            eventList.add(Event.builder()
                    .eventType(timelineDO.getEventType())
                    .eventCode(timelineDO.getEventCode())
                    .val(timelineDO.getVal())
                    .val1(timelineDO.getVal1()).build());
        }

        return new PageResult(list.get(list.size() - 1).getId(), eventList);
    }

    @Override
    protected void markCurrentStamp(long stamp) {
        // TODO
    }

    @Override
    protected void clean() {
        // TODO
    }
}
