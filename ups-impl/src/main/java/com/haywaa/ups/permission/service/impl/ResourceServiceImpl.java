package com.haywaa.ups.permission.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haywaa.ups.cooperate.CooperateService;
import com.haywaa.ups.dao.ResourceDAO;
import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.rpc.enums.ResourceType;
import com.haywaa.ups.domain.entity.ResourceDO;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.permission.bo.OperatorInfo;
import com.haywaa.ups.permission.service.ModuleService;
import com.haywaa.ups.permission.service.OperateAuthCheckService;
import com.haywaa.ups.permission.service.ResourceService;
import com.haywaa.ups.permission.service.SystemService;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-26 14:53
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceDAO resourceDAO;

    @Autowired
    private SystemService systemService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private CooperateService cooperateService;

    @Autowired
    private OperateAuthCheckService operateAuthCheckService;

    @Override
    public List<ResourceDO> selectAll(String systemCode, String status) {
        if (systemCode == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "系统编号必填");
        }

        return resourceDAO.selectAll(systemCode, status);
    }

    @Override
    public Integer insert(ResourceDO resourceDO, OperatorInfo operator) {
        // 仅超级管理, UPS管理员或系统管理员可操作
        if (!operateAuthCheckService.isUpsAdmin(operator.getUserId(), operator.getChannel())
                && ! operateAuthCheckService.isSystemAdmin(operator.getUserId(), operator.getChannel(), resourceDO.getSystemCode())) {
            throw ErrorCode.PERMISSION_DENIED.toBizException();
        }

        systemService.checkCodeIsValid(resourceDO.getSystemCode());
        moduleService.checkCodeIsValid(resourceDO.getSystemCode(), resourceDO.getModuleCode());
        checkParentCode(resourceDO.getSystemCode(), resourceDO.getParentCode());

        resourceDO.setCreator(operator.getOperatorCode());
        resourceDO.setModifier(operator.getOperatorCode());
        cooperateService.publishResourceWillUpdate(resourceDO.getSystemCode(), resourceDO.getCode());
        resourceDAO.insert(resourceDO);
        cooperateService.publishResourceUpdated(resourceDO.getSystemCode(), resourceDO.getCode());
        return resourceDO.getId();
    }

    @Override
    public void update(ResourceDO resourceDO, OperatorInfo operator) {
        if (resourceDO.getId() == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：资源ID不能为空");
        }

        ResourceDO resourceInDb = resourceDAO.selectById(resourceDO.getId());
        if (resourceInDb == null) {
            return;
        }

        // 仅超级管理, UPS管理员或系统管理员可操作
        if (!operateAuthCheckService.isUpsAdmin(operator.getUserId(), operator.getChannel())
                && ! operateAuthCheckService.isSystemAdmin(operator.getUserId(), operator.getChannel(), resourceInDb.getSystemCode())) {
            throw ErrorCode.PERMISSION_DENIED.toBizException();
        }

        checkParentCode(resourceInDb.getSystemCode(), resourceDO.getParentCode());

        resourceDO.setCode(null);
        resourceDO.setSystemCode(null);
        resourceDO.setModuleCode(null);
        resourceDO.setType(null);
        resourceDO.setModifier(operator.getOperatorCode());
        cooperateService.publishResourceWillUpdate(resourceInDb.getSystemCode(), resourceDO.getCode());
        resourceDAO.update(resourceDO);
        cooperateService.publishResourceUpdated(resourceInDb.getSystemCode(), resourceDO.getCode());
    }

    public void checkParentCode(String systemCode, String parentCode) {
        if (parentCode == null) {
            return;
        }

        ResourceDO resourceDO = resourceDAO.selectByCode(systemCode, parentCode);
        if (resourceDO == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：父资源不存在");
        }

        if (!ResourceType.GROUP.toString().equals(resourceDO.getType())) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：父资源类型只能为分组");
        }
    }
}
