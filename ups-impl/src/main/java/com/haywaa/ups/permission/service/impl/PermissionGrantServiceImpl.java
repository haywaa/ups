package com.haywaa.ups.permission.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.haywaa.ups.cooperate.bo.UserEventBO;
import com.haywaa.ups.dao.RoleDAO;
import com.haywaa.ups.dao.UserRoleDAO;
import com.haywaa.ups.permission.bo.OperatorInfo;
import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.constants.ValidStatus;
import com.haywaa.ups.domain.entity.RoleDO;
import com.haywaa.ups.domain.entity.UserRoleDO;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.rpc.request.UserRoleDelReq;
import com.haywaa.ups.rpc.request.UserRoleGrantReq;
import com.haywaa.ups.cooperate.CooperateService;
import com.haywaa.ups.permission.service.PermissionGrantService;
import com.haywaa.ups.permission.service.RoleService;
import com.haywaa.ups.permission.service.SystemService;
import com.haywaa.ups.user.UserService;
import com.haywaa.ups.user.bo.UserCheckBO;
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

    @Autowired
    private UserService userService;

    @Override
    public void grantPermission(UserRoleGrantReq grantReq, OperatorInfo operator) {
        if (operator == null ) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交操作人信息");
        }

        final String systemCode = grantReq.getSystemCode();
        if (StringUtils.isBlank(systemCode)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交系统编号");
        }

        List<UserRoleGrantReq.UserRoleItem> userRoleItemList = grantReq.getUserRoleItemList();
        if (CollectionUtils.isEmpty(userRoleItemList)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交待授权角色");
        }

        systemService.checkCodeIsValid(grantReq.getSystemCode());

        userService.checkChannelUser(grantReq.getUserRoleItemList().stream().map(item -> {
            if (item.getUserId() == null || item.getChannel() == null) {
                throw ErrorCode.INVALID_PARAM.toBizException("用户ID于用户来源渠道必填");
            }

            if (item.getRoleCode() == null) {
                throw ErrorCode.INVALID_PARAM.toBizException("待授权角色ID不能为空");
            }

            UserCheckBO checkBO = new UserCheckBO();
            checkBO.setUserId(item.getUserId());
            checkBO.setChannel(item.getChannel());
            return checkBO;
        }).collect(Collectors.toList()));

        // 仅超级管理与或系统管理员可操作
        List<RoleDO> handlerRoles = roleService.selectValidRolesByUserId(operator.getUserId());
        if (AuthUtil.isSuperAdmin(handlerRoles)
                || AuthUtil.isSystemAdmin(handlerRoles, systemCode)) {
            throw new BizException(ErrorCode.PERMISSION_DENIED.getErrorNo(), "无操作权限");
        }

        List<RoleDO> systemRoleList = roleDAO.selectBySystemCode(systemCode, ValidStatus.VALID.toString());
        if (CollectionUtils.isEmpty(systemRoleList)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的角色ID");
        }

        Map<String, Integer> systemRoleMap = systemRoleList.stream().collect(Collectors.toMap(RoleDO::getCode, RoleDO::getId));

        List<UserRoleDO> addUserRoleList = userRoleItemList.stream().map(userRoleItem -> {
            Integer roleId = systemRoleMap.get(userRoleItem.getRoleCode());
            if (roleId == null) {
                throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的角色ID：" + userRoleItem.getRoleCode());
            }

            UserRoleDO userRoleDO = new UserRoleDO();
            userRoleDO.setUserId(userRoleItem.getUserId());
            userRoleDO.setChannel(userRoleItem.getChannel());
            userRoleDO.setRoleId(roleId);
            userRoleDO.setRelatedKey(userRoleItem.getRelatedKey());
            userRoleDO.setSystemCode(systemCode);
            userRoleDO.setCreator(operator.getOperatorCode());
            userRoleDO.setModifier(operator.getOperatorCode());
            return userRoleDO;
        }).collect(Collectors.toList());

        List<UserEventBO> eventBOList = addUserRoleList.stream().map(item -> {
            UserEventBO eventBO = new UserEventBO();
            eventBO.setChannel(item.getChannel());
            eventBO.setUserId(item.getUserId());
            eventBO.setSystemCode(systemCode);
            return eventBO;
        }).collect(Collectors.toList());

        cooperateService.publishUserPermissionWillUpdate(eventBOList);
        userRoleDAO.insertList(addUserRoleList);
        cooperateService.publishUserPermissionUpdated(eventBOList);
    }

    @Override
    public void removeRoles(UserRoleDelReq delReq, OperatorInfo operator) {
        if (operator == null ) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交操作人信息");
        }

        final String systemCode = delReq.getSystemCode();
        if (StringUtils.isBlank(systemCode)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交系统编号");
        }

        if (delReq.getUserId() == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交用户ID");
        }

        if (StringUtils.isBlank(delReq.getChannel())) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交用户所属渠道");
        }

        systemService.checkCodeIsValid(delReq.getSystemCode());

        // 仅超级管理与或系统管理员可操作
        List<RoleDO> handlerRoles = roleService.selectValidRolesByUserId(operator.getUserId());
        if (AuthUtil.isSuperAdmin(handlerRoles)
                || AuthUtil.isSystemAdmin(handlerRoles, delReq.getSystemCode())) {
            throw new BizException(ErrorCode.PERMISSION_DENIED.getErrorNo(), "无操作权限");
        }

        List<RoleDO> systemRoleList = roleDAO.selectBySystemCode(delReq.getSystemCode(), ValidStatus.VALID.toString());
        if (CollectionUtils.isEmpty(systemRoleList)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请上传需要删除的角色");
        }

        Map<String, Integer> systemRoleMap = systemRoleList.stream().collect(Collectors.toMap(RoleDO::getCode, RoleDO::getId));

        List<String> roleItems = delReq.getRoleItems().stream().map(roleItem -> {
            if (StringUtils.isBlank(roleItem.getRoleCode())) {
                throw ErrorCode.INVALID_PARAM.toBizException("待删除角色Code不能为空");
            }

            Integer roleId = systemRoleMap.get(roleItem.getRoleCode());
            if (roleId == null) {
                throw ErrorCode.INVALID_PARAM.toBizException("无效的角色Code:" + roleItem.getRoleCode());
            }

            if (StringUtils.isBlank(roleItem.getRelatedKey())) {
                return roleId + ":0";
            }

            return roleId + ":" + roleItem.getRelatedKey();
        }).distinct().collect(Collectors.toList());

        UserEventBO eventBO = new UserEventBO();
        eventBO.setChannel(delReq.getChannel());
        eventBO.setUserId(delReq.getUserId());
        eventBO.setSystemCode(systemCode);
        List<UserEventBO> eventList = Arrays.asList(eventBO);

        cooperateService.publishUserPermissionWillUpdate(eventList);
        userRoleDAO.removeList(delReq.getUserId(), delReq.getChannel(), roleItems);
        cooperateService.publishUserPermissionUpdated(eventList);
    }
}
