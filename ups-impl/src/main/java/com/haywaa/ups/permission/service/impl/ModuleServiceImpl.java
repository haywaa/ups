package com.haywaa.ups.permission.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haywaa.ups.dao.ModuleDAO;
import com.haywaa.ups.permission.bo.OperatorInfo;
import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.constants.ValidStatus;
import com.haywaa.ups.domain.entity.ModuleDO;
import com.haywaa.ups.domain.entity.RoleDO;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.cooperate.CooperateService;
import com.haywaa.ups.permission.service.ModuleService;
import com.haywaa.ups.permission.service.OperateAuthCheckService;
import com.haywaa.ups.permission.service.RoleService;
import com.haywaa.ups.permission.service.SystemService;
import com.haywaa.ups.utils.AuthUtil;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-27 10:57
 */
@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private ModuleDAO moduleDAO;

    @Autowired
    private SystemService systemService;

    @Autowired
    private CooperateService cooperateService;

    @Autowired
    private OperateAuthCheckService operateAuthCheckService;

    @Override
    public List<ModuleDO> selectAll(String systemCode, String status) {
        if (systemCode == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "系统编号必填");
        }

        return moduleDAO.selectAll(systemCode, status);
    }

    @Override
    public Integer insert(ModuleDO moduleDO, OperatorInfo operator) {
        // 仅超级管理, UPS管理员或系统管理员可操作
        if (!operateAuthCheckService.isUpsAdmin(operator.getUserId(), operator.getChannel())
                && ! operateAuthCheckService.isSystemAdmin(operator.getUserId(), operator.getChannel(), moduleDO.getSystemCode())) {
            throw ErrorCode.PERMISSION_DENIED.toBizException();
        }

        systemService.checkCodeIsValid(moduleDO.getSystemCode());

        moduleDO.setCreator(operator.getOperatorCode());
        moduleDO.setModifier(operator.getOperatorCode());

        cooperateService.publishModuleWillUpdate(moduleDO.getSystemCode(), moduleDO.getCode());
        moduleDAO.insert(moduleDO);
        cooperateService.publishModuleUpdated(moduleDO.getSystemCode(), moduleDO.getCode());
        return moduleDO.getId();
    }

    @Override
    public void update(ModuleDO moduleDO, OperatorInfo operator) {
        if (moduleDO.getId() == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：业务模块ID不能为空");
        }

        ModuleDO moduleInDb = moduleDAO.selectById(moduleDO.getId());

        // 仅超级管理, UPS管理员或系统管理员可操作
        if (!operateAuthCheckService.isUpsAdmin(operator.getUserId(), operator.getChannel())
                && ! operateAuthCheckService.isSystemAdmin(operator.getUserId(), operator.getChannel(), moduleInDb.getSystemCode())) {
            throw ErrorCode.PERMISSION_DENIED.toBizException();
        }

        moduleDO.setCode(null);
        moduleDO.setSystemCode(null);
        moduleDO.setModifier(operator.getOperatorCode());

        cooperateService.publishModuleWillUpdate(moduleInDb.getSystemCode(), moduleDO.getCode());
        moduleDAO.update(moduleDO);
        cooperateService.publishModuleUpdated(moduleInDb.getSystemCode(), moduleDO.getCode());
    }

    @Override
    public void checkCodeIsValid(String systemCode, String code) {
        if (systemCode == null || code == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的业务模块编号：null");
        }

        ModuleDO moduleDO = moduleDAO.selectByCode(systemCode, code);
        if (moduleDO == null || !ValidStatus.VALID.toString().equals(moduleDO.getStatus())) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的业务模块编号（不存在或未启用）：" + code);
        }
    }
}
