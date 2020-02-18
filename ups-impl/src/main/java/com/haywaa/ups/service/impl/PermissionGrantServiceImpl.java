package com.haywaa.ups.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.haywaa.ups.dao.RoleDAO;
import com.haywaa.ups.dao.UserRoleDAO;
import com.haywaa.ups.domain.bo.OperatorInfo;
import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.constants.ValidStatus;
import com.haywaa.ups.domain.entity.RoleDO;
import com.haywaa.ups.domain.entity.UserRoleDO;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.service.CooperateService;
import com.haywaa.ups.service.PermissionGrantService;
import com.haywaa.ups.service.RoleService;
import com.haywaa.ups.service.SystemService;
import com.haywaa.ups.utils.AuthUtil;

/**
 * @description
 * @author: haywaa
 * @create: 2019-12-03 11:14
 */
@Service
public class PermissionGrantServiceImpl implements PermissionGrantService {

    @Autowired
    private CooperateService cooperateService;

    @Autowired
    private UserRoleDAO userRoleDAO;

    @Autowired
    private SystemService systemService;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private RoleService roleService;

    @Override
    public void grantPermission(Long userId, String systemCode, List<Integer> roleIds, OperatorInfo operator) {
        if (operator == null ) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交操作人信息");
        }

        if (systemCode == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交系统编号");
        }

        if (userId == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交被授权用户ID");
        }

        if (CollectionUtils.isEmpty(roleIds)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交需要分配的角色");
        }

        systemService.checkCodeIsValid(systemCode);

        // 仅超级管理与或系统管理员可操作
        List<RoleDO> handlerRoles = roleService.selectValidRolesByUserId(operator.getUserId());
        if (AuthUtil.isSuperAdmin(handlerRoles)
                || AuthUtil.isSystemAdmin(handlerRoles, systemCode)) {
            throw new BizException(ErrorCode.PERMISSION_DENIED.getErrorNo(), "无操作权限");
        }

        List<RoleDO> systemRoleList = roleDAO.selectBySystemCode(systemCode, ValidStatus.VALID.getCode());
        if (CollectionUtils.isEmpty(systemRoleList)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的角色ID：" + StringUtils.collectionToDelimitedString(roleIds, ","));
        }

        Map<Integer, RoleDO> systemRoleMap = new HashMap<>((int)(systemRoleList.size()/0.75f) + 1);
        for (RoleDO roleDO : systemRoleList) {
            systemRoleMap.put(roleDO.getId(), roleDO);
        }

        for (Integer roleId : roleIds) {
            RoleDO roleDO = systemRoleMap.get(roleId);
            if (roleDO == null) {
                throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的角色ID：" + roleId);
            }
        }

        List<UserRoleDO> userRoleList = new ArrayList<>(roleIds.size());
        for (Integer roleId : roleIds) {
            UserRoleDO userRoleDO = new UserRoleDO();
            userRoleDO.setUserId(userId);
            userRoleDO.setRoleId(roleId);
            userRoleDO.setCreator(operator.getOperatorCode());
            userRoleDO.setModifier(operator.getOperatorCode());
            userRoleList.add(userRoleDO);
        }

        cooperateService.publishUserPermissionWillUpdate(userId, systemCode);
        userRoleDAO.insertList(userRoleList);
        cooperateService.publishUserPermissionUpdated(userId, systemCode);
    }

    @Override
    public void removeRoles(Long userId, String systemCode, List<Integer> roleIds, OperatorInfo operator) {
        systemService.checkCodeIsValid(systemCode);

        // 仅超级管理与或系统管理员可操作
        List<RoleDO> handlerRoles = roleService.selectValidRolesByUserId(operator.getUserId());
        if (AuthUtil.isSuperAdmin(handlerRoles)
                || AuthUtil.isSystemAdmin(handlerRoles, systemCode)) {
            throw new BizException(ErrorCode.PERMISSION_DENIED.getErrorNo(), "无操作权限");
        }

        List<RoleDO> systemRoleList = roleDAO.selectBySystemCode(systemCode, ValidStatus.VALID.getCode());
        if (CollectionUtils.isEmpty(systemRoleList)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的角色ID：" + StringUtils.collectionToDelimitedString(roleIds, ","));
        }

        Map<Integer, RoleDO> systemRoleMap = new HashMap<>((int)(systemRoleList.size()/0.75f) + 1);
        for (RoleDO roleDO : systemRoleList) {
            systemRoleMap.put(roleDO.getId(), roleDO);
        }

        Iterator<Integer> it = roleIds.iterator();
        while (it.hasNext()) {
            Integer roleId = it.next();
            RoleDO roleDO = systemRoleMap.get(roleId);
            if (roleDO == null) {
                it.remove();
            }
        }

        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }

        cooperateService.publishUserPermissionWillUpdate(userId, systemCode);
        userRoleDAO.removeList(userId, roleIds);
        cooperateService.publishUserPermissionUpdated(userId, systemCode);
    }
}
