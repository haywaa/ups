package com.haywaa.ups.permission.service;

import com.haywaa.ups.rpc.dto.UserPermissionDTO;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 17:36
 */
public interface PermissionQueryService {

    /**
     *
     */
    UserPermissionDTO queryUserPermission(String systemCode, String channel, String thirdId);

    void handleSystemUpdatedEvent(Integer systemId, String systemCode);

    void handleModuleUpdatedEvent(String systemCode, String moduleCode);

    void handleResourceUpdatedEvent(String systemCode, String resourceCode);

    void handleRoleUpdatedEvent(String systemCode, String roleCode);

    void handleRoleResourceUpdatedEvent(Integer roleId);

    void handleUserPermissionUpdatedEvent(Long userId, String channel, String systemCode);
}
