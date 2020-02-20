package com.haywaa.ups.cooperate.event;

import java.util.List;

import com.haywaa.ups.cooperate.event.entity.Event;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-27 18:04
 */
public interface EventTimeline {

    void pushEvent(Event event);

    void pushEvent(List<Event> eventList);
}
