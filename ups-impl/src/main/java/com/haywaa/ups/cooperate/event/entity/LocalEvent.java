package com.haywaa.ups.cooperate.event.entity;

import org.springframework.context.ApplicationEvent;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-28 09:49
 */
public class LocalEvent extends ApplicationEvent {

    private final Event event;

    public LocalEvent(Event event) {
        super("localEvent");
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
