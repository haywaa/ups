package com.haywaa.ups.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haywaa.ups.dao.SystemDAO;
import com.haywaa.ups.domain.bo.OperatorInfo;
import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.constants.ValidStatus;
import com.haywaa.ups.domain.entity.RoleDO;
import com.haywaa.ups.domain.entity.SystemDO;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.service.CooperateService;
import com.haywaa.ups.service.RoleService;
import com.haywaa.ups.service.SystemService;
import com.haywaa.ups.utils.AuthUtil;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-27 10:08
 */
@Service
public class SystemServiceImpl implements SystemService {

    @Autowired
    private SystemDAO systemDAO;

    @Autowired
    private CooperateService cooperateService;

    @Autowired
    private RoleService roleService;

    @Override
    public List<SystemDO> selectAll(Integer status) {
        return systemDAO.selectAll(status);
    }

    @Override
    public Integer insert(SystemDO systemDO, OperatorInfo operator) {
        // 仅超级管理与或系统管理员可操作
        List<RoleDO> handlerRoles = roleService.selectValidRolesByUserId(operator.getUserId());
        if (AuthUtil.isSuperAdmin(handlerRoles)) {
            throw new BizException(ErrorCode.PERMISSION_DENIED.getErrorNo(), "无操作权限");
        }

        systemDO.setCreator(operator.getOperatorCode());
        systemDO.setModifier(operator.getOperatorCode());

        cooperateService.publishSystemWillUpdate(systemDO.getId(), systemDO.getCode());
        systemDAO.insert(systemDO);
        cooperateService.publishSystemUpdated(systemDO.getId(), systemDO.getCode());
        return systemDO.getId();
    }

    @Override
    public void update(SystemDO systemDO, OperatorInfo operator) {
        if (systemDO.getId() == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：系统ID不能为空");
        }

        // 仅超级管理与或系统管理员可操作
        List<RoleDO> handlerRoles = roleService.selectValidRolesByUserId(operator.getUserId());
        if (AuthUtil.isSuperAdmin(handlerRoles)
                || AuthUtil.isSystemAdmin(handlerRoles, systemDO.getCode())) {
            throw new BizException(ErrorCode.PERMISSION_DENIED.getErrorNo(), "无操作权限");
        }

        systemDO.setCode(null);
        systemDO.setModifier(operator.getOperatorCode());

        cooperateService.publishSystemWillUpdate(systemDO.getId(), systemDO.getCode());
        systemDAO.update(systemDO);
        cooperateService.publishSystemUpdated(systemDO.getId(), systemDO.getCode());
    }

    @Override
    public void checkCodeIsValid(String code) {
        if (code == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的系统编号：null");
        }

        SystemDO systemDO = systemDAO.selectByCode(code);
        if (systemDO == null || systemDO.getStatus() == ValidStatus.INVALID.getCode()) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的系统编号（不存在或未启用）：" + code);
        }
    }
}
