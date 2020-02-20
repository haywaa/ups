package com.haywaa.ups.cooperate;

import java.util.List;

import com.haywaa.ups.cooperate.bo.UserEventBO;

/**
 * 分布式集群节点本地缓存同步协调服务
 *
 * @author: haywaa
 * @create: 2019-11-28 09:31
 */
public interface CooperateService {

    void publishSystemWillUpdate(Integer systemId, String code);

    void publishSystemUpdated(Integer systemId, String code);

    void publishModuleWillUpdate(String systemCode, String code);

    void publishModuleUpdated(String systemCode, String code);

    void publishResourceWillUpdate(String systemCode, String code);

    void publishResourceUpdated(String systemCode, String code);

    void publishRoleWillUpdate(String systemCode, String code);

    void publishRoleUpdated(String systemCode, String code);

    void publishRoleResourceWillUpdate(Integer roleId);

    void publishRoleResourceUpdated(Integer roleId);

    void publishUserPermissionWillUpdate(List<UserEventBO> eventBoList);

    void publishUserPermissionUpdated(List<UserEventBO> eventBoList);
}
