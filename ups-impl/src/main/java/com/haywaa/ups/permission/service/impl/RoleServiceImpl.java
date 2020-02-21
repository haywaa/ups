package com.haywaa.ups.permission.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.haywaa.ups.dao.ResourceDAO;
import com.haywaa.ups.dao.RoleDAO;
import com.haywaa.ups.dao.RoleResourceDAO;
import com.haywaa.ups.dao.UserRoleDAO;
import com.haywaa.ups.domain.constants.RoleType;
import com.haywaa.ups.domain.entity.UserRoleDO;
import com.haywaa.ups.permission.bo.OperatorInfo;
import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.constants.ResourceType;
import com.haywaa.ups.domain.constants.ValidStatus;
import com.haywaa.ups.domain.entity.ResourceDO;
import com.haywaa.ups.domain.entity.RoleDO;
import com.haywaa.ups.domain.entity.RoleResourceDO;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.domain.query.RoleQuery;
import com.haywaa.ups.cooperate.CooperateService;
import com.haywaa.ups.permission.service.ModuleService;
import com.haywaa.ups.permission.service.OperateAuthCheckService;
import com.haywaa.ups.permission.service.RoleService;
import com.haywaa.ups.permission.service.SystemService;
import com.haywaa.ups.utils.AuthUtil;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-29 15:34
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private CooperateService cooperateService;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserRoleDAO userRoleDAO;

    @Autowired
    private ResourceDAO resourceDAO;

    @Autowired
    private RoleResourceDAO roleResourceDAO;

    @Autowired
    private SystemService systemService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private OperateAuthCheckService operateAuthCheckService;


    @Override
    public Integer insert(RoleDO roleDO, OperatorInfo operator) {
        // 仅超级管理, UPS管理员或系统管理员可操作
        if (!operateAuthCheckService.isUpsAdmin(operator.getUserId(), operator.getChannel())
                && ! operateAuthCheckService.isSystemAdmin(operator.getUserId(), operator.getChannel(), roleDO.getSystemCode())) {
            throw ErrorCode.PERMISSION_DENIED.toBizException();
        }

        if (RoleType.ROOT.toString().equals(roleDO.getType())) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "超级管理员角色不能新增");
        }

        systemService.checkCodeIsValid(roleDO.getSystemCode());
        if (roleDO.getModuleCode() != null) {
            moduleService.checkCodeIsValid(roleDO.getSystemCode(), roleDO.getModuleCode());
        }

        roleDO.setCreator(operator.getOperatorCode());
        roleDO.setModifier(operator.getOperatorCode());
        cooperateService.publishRoleWillUpdate(roleDO.getSystemCode(), roleDO.getCode());
        roleDAO.insert(roleDO);
        cooperateService.publishRoleUpdated(roleDO.getSystemCode(), roleDO.getCode());
        return roleDO.getId();
    }

    @Override
    public void update(RoleDO roleDO, OperatorInfo operator) {
        RoleDO roleIndb = checkValidRoleId(roleDO.getId());
        if (roleIndb == null) {
            return;
        }

        // 仅超级管理, UPS管理员或系统管理员可操作
        if (!operateAuthCheckService.isUpsAdmin(operator.getUserId(), operator.getChannel())
                && ! operateAuthCheckService.isSystemAdmin(operator.getUserId(), operator.getChannel(), roleIndb.getSystemCode())) {
            throw ErrorCode.PERMISSION_DENIED.toBizException();
        }

        if (RoleType.ROOT.toString().equals(roleIndb.getType())) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "超级管理员角色不能被修改");
        }

        if (roleDO.getSystemCode() != null && !roleDO.getSystemCode().equals(roleIndb.getSystemCode())) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：系统编号不允许修改");
        }

        if (roleDO.getModuleCode() != null && !roleDO.getModuleCode().equals(roleIndb.getModuleCode())) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：业务模块编号不允许修改");
        }

        roleDO.setSystemCode(null);
        roleDO.setModuleCode(null);
        roleDO.setModifier(operator.getOperatorCode());
        cooperateService.publishRoleWillUpdate(roleIndb.getSystemCode(), roleDO.getCode());
        roleDAO.update(roleDO);
        cooperateService.publishRoleUpdated(roleIndb.getSystemCode(), roleDO.getCode());
    }

    @Override
    public void addRoleResourceIds(Integer roleId, List<Integer> resourceIds, OperatorInfo operator) {
        if (roleId == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交被分配资源的角色ID");
        }

        if (CollectionUtils.isEmpty(resourceIds)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交需被分配的资源");
        }

        RoleDO roleIndb = checkValidRoleId(roleId);
        // 仅超级管理, UPS管理员或系统管理员可操作
        if (!operateAuthCheckService.isUpsAdmin(operator.getUserId(), operator.getChannel())
                && ! operateAuthCheckService.isSystemAdmin(operator.getUserId(), operator.getChannel(), roleIndb.getSystemCode())) {
            throw ErrorCode.PERMISSION_DENIED.toBizException();
        }

        List<ResourceDO> systemResourceList = resourceDAO.selectBySystemCode(roleIndb.getSystemCode(), ValidStatus.VALID.toString());
        if (CollectionUtils.isEmpty(systemResourceList)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的资源ID：" + StringUtils.collectionToDelimitedString(resourceIds, ","));
        }

        Map<Integer, ResourceDO> systemResourceMap = new HashMap<>((int)(systemResourceList.size() / 0.75f) + 1);
        for (ResourceDO resourceDO : systemResourceList) {
            systemResourceMap.put(resourceDO.getId(), resourceDO);
        }

        Iterator<Integer> it = resourceIds.iterator();
        while (it.hasNext()) {
            Integer resourceId = it.next();

            ResourceDO resourceDO = systemResourceMap.get(resourceId);
            if (resourceDO == null) {
                throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的资源ID：" + resourceId);
            }

            // 对Action授权，不能对菜单进行授权
            if (resourceDO.getType() == ResourceType.GROUP.getCode()) {
                it.remove();
                continue;
            }
        }

        List<RoleResourceDO> roleResourceList = new ArrayList<>(resourceIds.size());
        for (Integer resourceId : resourceIds) {
            RoleResourceDO roleResourceDO = new RoleResourceDO();
            roleResourceDO.setRoleId(roleId);
            roleResourceDO.setResourceId(resourceId);
            roleResourceDO.setCreator(operator.getOperatorCode());
            roleResourceDO.setModifier(operator.getOperatorCode());
        }

        cooperateService.publishRoleResourceWillUpdate(roleId);
        roleResourceDAO.insertList(roleResourceList);
        cooperateService.publishRoleResourceUpdated(roleId);
    }

    @Override
    public List<Integer> queryResourceIds(Integer roleId) {
        if (roleId == null) {
            return null;
        }

        return roleResourceDAO.selectResourceIds(roleId);
    }

    @Override
    public List<RoleDO> selectValidRolesByUserId(Long userId, String channel, String systemCode) {
        if (userId == null || StringUtils.isEmpty(channel) || StringUtils.isEmpty(systemCode)) {
            return null;
        }

        List<UserRoleDO> userRoleDOList = userRoleDAO.selectByUserId(userId, channel, systemCode);
        if (CollectionUtils.isEmpty(userRoleDOList)) {
            return null;
        }

        List<Integer> roleIds = userRoleDOList.stream().map(UserRoleDO::getRoleId).collect(Collectors.toList());
        return roleDAO.selectByIds(roleIds, ValidStatus.VALID.toString());
    }

    private RoleDO checkValidRoleId(Integer roleId) {
        if (roleId == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：角色ID不能为空");
        }

        RoleDO roleIndb = roleDAO.selectById(roleId);
        if (roleIndb == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：无效的角色ID");
        }

        return roleIndb;
    }

    @Override
    public List<RoleDO> selectAll(RoleQuery query) {
        return roleDAO.selectAll(query);
    }
}
