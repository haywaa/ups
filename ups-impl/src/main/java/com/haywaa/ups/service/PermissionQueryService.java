package com.haywaa.ups.service;

import com.haywaa.ups.domain.bo.UserPermissionBO;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 17:36
 */
public interface PermissionQueryService {

    /**
     *
     */
    UserPermissionBO queryUserPermission(String systemCode, Long userId);

    void handleSystemUpdatedEvent(Integer systemId, String systemCode);

    void handleModuleUpdatedEvent(String systemCode, String moduleCode);

    void handleResourceUpdatedEvent(String systemCode, String resourceCode);

    void handleRoleUpdatedEvent(String systemCode, String roleCode);

    void handleRoleResourceUpdatedEvent(Integer roleId);

    void handleUserPermissionUpdatedEvent(Long userId, String systemCode);
}
