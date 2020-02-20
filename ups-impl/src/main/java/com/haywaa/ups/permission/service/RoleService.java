package com.haywaa.ups.permission.service;

import java.util.List;

import com.haywaa.ups.permission.bo.OperatorInfo;
import com.haywaa.ups.domain.entity.RoleDO;
import com.haywaa.ups.domain.query.RoleQuery;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-26 16:35
 */
public interface RoleService {

    Integer insert(RoleDO roleDO, OperatorInfo operator);

    void update(RoleDO roleDO, OperatorInfo operator);

    /**
     * 分配资源
     * ☆ 角色授予资源权限时，不单独对菜单授权，只对菜单下的操作进行授权，拥有了操作的权限即用了了所属菜单的权限，即仅对菜单授权是无意义的
     */
    void addRoleResourceIds(Integer roleId, List<Integer> resourceIds, OperatorInfo operator);

    /**
     * 获取已分配的系统资源, 含已禁用资源
     */
    List<Integer> queryResourceIds(Integer roleId);

    List<RoleDO> selectValidRolesByUserId(Long userId);

    List<RoleDO> selectAll(RoleQuery query);
}
