package com.haywaa.ups.domain.entity;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-12-02 17:50
 */
@Data
public class EventTimelineDO extends BaseDO<Long> {

    private int eventType;

    private String eventCode;

    private Object val;

    private Object val1;

    private Object val2;
}
