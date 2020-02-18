package com.haywaa.ups.event.entity;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-27 18:15
 */
@Builder
@Data
public class Event {

    private int eventType;

    private String eventCode;

    private Object val;

    private Object val1;

    public enum EventType {
        /**
         * 即将发生
         * 即将发生事件可用于监听方做补偿
         */
        WILL(0),

        /**
         * 已发生
         */
        DONE(1);

        private final int code;
        EventType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
