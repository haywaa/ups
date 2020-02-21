package com.haywaa.ups.permission.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.haywaa.ups.domain.constants.Constants;
import com.haywaa.ups.domain.entity.RoleDO;
import com.haywaa.ups.permission.service.OperateAuthCheckService;
import com.haywaa.ups.permission.service.RoleService;
import com.haywaa.ups.utils.AuthUtil;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-21 16:13
 */
@Service
public class OperateAuthCheckServiceImpl implements OperateAuthCheckService {

    @Autowired
    private RoleService roleService;

    @Override
    public boolean isUpsAdmin(Long userId, String channel) {
        if (userId == null || StringUtils.isBlank(channel)) {
            return false;
        }

        List<RoleDO> roleDOList = roleService.selectValidRolesByUserId(userId, channel, Constants.SYSTEM_UPS);
        if (CollectionUtils.isEmpty(roleDOList)) {
            return false;
        }

        for (RoleDO role : roleDOList) {
            if (AuthUtil.isSystemAdmin(role, Constants.SYSTEM_UPS)) {
                return true;
            }

            if (AuthUtil.isSuperAdmin(role)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isSystemAdmin(Long userId, String channel, String systemCode) {
        if (userId == null || StringUtils.isBlank(channel) || StringUtils.isBlank(systemCode)) {
            return false;
        }

        List<RoleDO> roleDOList = roleService.selectValidRolesByUserId(userId, channel, systemCode);
        if (CollectionUtils.isEmpty(roleDOList)) {
            return false;
        }

        for (RoleDO role : roleDOList) {
            if (AuthUtil.isSystemAdmin(role, systemCode)) {
                return true;
            }
        }

        return false;
    }
}
