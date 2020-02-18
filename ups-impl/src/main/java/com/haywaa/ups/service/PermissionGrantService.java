package com.haywaa.ups.service;

import java.util.List;

import com.haywaa.ups.domain.bo.OperatorInfo;

/**
 * @description
 * @author: haywaa
 * @create: 2019-12-03 11:14
 */
public interface PermissionGrantService {

    /**
     * 分配角色权限
     */
    void grantPermission(Long userId, String systemCode, List<Integer> roleIds, OperatorInfo operator);

    /**
     * 移除用户角色
     */
    void removeRoles(Long userId, String systemCode, List<Integer> roleIds, OperatorInfo operator);
}
