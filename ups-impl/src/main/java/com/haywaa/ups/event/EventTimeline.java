package com.haywaa.ups.event;

import com.haywaa.ups.event.entity.Event;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-27 18:04
 */
public interface EventTimeline {

    void pushEvent(Event event);
}
