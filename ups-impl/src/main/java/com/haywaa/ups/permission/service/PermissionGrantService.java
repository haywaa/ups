package com.haywaa.ups.permission.service;

import com.haywaa.ups.permission.bo.OperatorInfo;
import com.haywaa.ups.rpc.request.UserRoleDelReq;
import com.haywaa.ups.rpc.request.UserRoleGrantReq;

/**
 * @description
 * @author: haywaa
 * @create: 2019-12-03 11:14
 */
public interface PermissionGrantService {

    /**
     * 分配角色权限
     */
    void grantPermission(UserRoleGrantReq grantReq, OperatorInfo operator);

    /**
     * 移除用户角色
     */
    void removeRoles(UserRoleDelReq delReq, OperatorInfo operator);
}
