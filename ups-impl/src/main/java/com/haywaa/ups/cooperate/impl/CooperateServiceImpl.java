package com.haywaa.ups.cooperate.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.haywaa.ups.cooperate.bo.UserEventBO;
import com.haywaa.ups.cooperate.event.EventTimeline;
import com.haywaa.ups.cooperate.event.entity.Event;
import com.haywaa.ups.cooperate.event.entity.LocalEvent;
import com.haywaa.ups.cooperate.CooperateService;
import com.haywaa.ups.permission.service.PermissionQueryService;
import com.haywaa.ups.utils.LangUtil;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-28 09:32
 */
@Service
public class CooperateServiceImpl implements CooperateService, ApplicationListener<LocalEvent> {

    private static final Logger logger = LoggerFactory.getLogger(CooperateServiceImpl.class);

    private static final String EVENT_SYSTEM_UPDATE = "system_update";

    private static final String EVENT_MODULE_UPDATE = "module_update";

    private static final String EVENT_RESOURCE_UPDATE = "role_update";

    private static final String EVENT_ROLE_UPDATE = "role_update";

    private static final String EVENT_ROLE_RESOURCE_UPDATE = "role_resource_update";

    private static final String EVENT_USER_PERMISSION_UPDATE = "user_permission_update";

    @Autowired
    private EventTimeline eventTimeline;

    @Autowired
    private PermissionQueryService permissionService;

    @Override
    public void onApplicationEvent(LocalEvent localEvent) {
        Event event = localEvent.getEvent();
        if (event == null) {
            return;
        }

        if (event.getEventType() == Event.EventType.WILL.getCode()) {
            // TODO 加入本地延迟队列，可被Done事件抵消
            return;
        }

        // TODO 抵消本地延迟队列中对应的Will事件
        handleEvent(event);
    }

    private void handleEvent(Event event) {
        if (EVENT_SYSTEM_UPDATE.equals(event.getEventCode())) {
            permissionService.handleSystemUpdatedEvent(parseEventValueToInteger(event), LangUtil.parseString(event.getVal1()));
        } else if (EVENT_MODULE_UPDATE.equals(event.getEventCode())) {
            permissionService.handleModuleUpdatedEvent(LangUtil.parseString(event.getVal()), LangUtil.parseString(event.getVal1()));
        } else if (EVENT_RESOURCE_UPDATE.equals(event.getEventCode())) {
            permissionService.handleResourceUpdatedEvent(LangUtil.parseString(event.getVal()), LangUtil.parseString(event.getVal1()));
        } else if (EVENT_ROLE_UPDATE.equals(event.getEventCode())) {
            permissionService.handleRoleUpdatedEvent(LangUtil.parseString(event.getVal()), LangUtil.parseString(event.getVal1()));
        } else if (EVENT_ROLE_RESOURCE_UPDATE.equals(event.getEventCode())) {
            permissionService.handleRoleResourceUpdatedEvent(parseEventValueToInteger(event));
        } else if (EVENT_USER_PERMISSION_UPDATE.equals(event.getEventCode())) {
            permissionService.handleUserPermissionUpdatedEvent(LangUtil.parseLong(event.getVal(), null), String.valueOf(event.getVal1()), String.valueOf(event.getVal2()));
        }
    }

    private Integer parseEventValueToInteger(Event event) {
        return LangUtil.parseInteger(event.getVal(), null);
    }

    @Override
    public void publishSystemWillUpdate(Integer systemId, String code) {
        eventTimeline.pushEvent(Event.builder()
                .eventType(Event.EventType.WILL.getCode())
                .eventCode(EVENT_SYSTEM_UPDATE)
                .val(systemId)
                .val1(code).build());
    }

    @Override
    public void publishSystemUpdated(Integer systemId, String code) {
        eventTimeline.pushEvent(Event.builder()
                .eventType(Event.EventType.DONE.getCode())
                .eventCode(EVENT_SYSTEM_UPDATE)
                .val(systemId)
                .val1(code).build());
    }

    @Override
    public void publishModuleWillUpdate(String systemCode, String code) {
        eventTimeline.pushEvent(Event.builder()
                .eventType(Event.EventType.WILL.getCode())
                .eventCode(EVENT_MODULE_UPDATE)
                .val(systemCode)
                .val1(code).build());
    }

    @Override
    public void publishModuleUpdated(String systemCode, String code) {
        eventTimeline.pushEvent(Event.builder()
                .eventType(Event.EventType.DONE.getCode())
                .eventCode(EVENT_MODULE_UPDATE)
                .val(systemCode)
                .val1(code).build());
    }

    @Override
    public void publishResourceWillUpdate(String systemCode, String code) {
        eventTimeline.pushEvent(Event.builder()
                .eventType(Event.EventType.WILL.getCode())
                .eventCode(EVENT_RESOURCE_UPDATE)
                .val(systemCode)
                .val1(code).build());
    }

    @Override
    public void publishResourceUpdated(String systemCode, String code) {
        eventTimeline.pushEvent(Event.builder()
                .eventType(Event.EventType.DONE.getCode())
                .eventCode(EVENT_RESOURCE_UPDATE)
                .val(systemCode)
                .val1(code).build());
    }

    @Override
    public void publishRoleWillUpdate(String systemCode, String code) {
        eventTimeline.pushEvent(Event.builder()
                .eventType(Event.EventType.WILL.getCode())
                .eventCode(EVENT_ROLE_UPDATE)
                .val(systemCode)
                .val1(code).build());
    }

    @Override
    public void publishRoleUpdated(String systemCode, String code) {
        eventTimeline.pushEvent(Event.builder()
                .eventType(Event.EventType.DONE.getCode())
                .eventCode(EVENT_ROLE_UPDATE)
                .val(systemCode)
                .val1(code).build());
    }

    @Override
    public void publishRoleResourceWillUpdate(Integer roleId) {
        eventTimeline.pushEvent(Event.builder()
                .eventType(Event.EventType.WILL.getCode())
                .eventCode(EVENT_ROLE_RESOURCE_UPDATE)
                .val(roleId).build());
    }

    @Override
    public void publishRoleResourceUpdated(Integer roleId) {
        eventTimeline.pushEvent(Event.builder()
                .eventType(Event.EventType.DONE.getCode())
                .eventCode(EVENT_ROLE_RESOURCE_UPDATE)
                .val(roleId).build());
    }

    @Override
    public void publishUserPermissionWillUpdate(List<UserEventBO> eventBoList) {
        if (CollectionUtils.isEmpty(eventBoList)) {
            return;
        }
        List<Event> eventList = eventBoList.stream().map(item -> Event.builder()
                .eventType(Event.EventType.WILL.getCode())
                .eventCode(EVENT_USER_PERMISSION_UPDATE)
                .val(item.getUserId())
                .val1(item.getChannel())
                .val2(item.getSystemCode()).build()).collect(Collectors.toList());
        eventTimeline.pushEvent(eventList);
    }

    @Override
    public void publishUserPermissionUpdated(List<UserEventBO> eventBoList) {
        if (CollectionUtils.isEmpty(eventBoList)) {
            return;
        }
        List<Event> eventList = eventBoList.stream().map(item -> Event.builder()
                .eventType(Event.EventType.DONE.getCode())
                .eventCode(EVENT_USER_PERMISSION_UPDATE)
                .val(item.getUserId())
                .val1(item.getChannel())
                .val2(item.getSystemCode()).build()).collect(Collectors.toList());
        eventTimeline.pushEvent(eventList);
    }
}
