package com.haywaa.ups.permission.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haywaa.ups.cooperate.CooperateService;
import com.haywaa.ups.dao.SystemDAO;
import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.constants.ValidStatus;
import com.haywaa.ups.domain.entity.SystemDO;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.permission.bo.OperatorInfo;
import com.haywaa.ups.permission.service.OperateAuthCheckService;
import com.haywaa.ups.permission.service.SystemService;

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
    private OperateAuthCheckService operateAuthCheckService;

    @Override
    public List<SystemDO> selectAll(String status) {
        return systemDAO.selectAll(status);
    }

    @Override
    public Integer insert(SystemDO systemDO, OperatorInfo operator) {
        // 仅超级管理, UPS管理员可操作
        if (!operateAuthCheckService.isUpsAdmin(operator.getUserId(), operator.getChannel())) {
            throw ErrorCode.PERMISSION_DENIED.toBizException();
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

        SystemDO systemDoInDb = systemDAO.selectById(systemDO.getId());
        if (systemDoInDb == null) {
            return;
        }

        // 仅超级管理, UPS管理员可操作
        if (!operateAuthCheckService.isUpsAdmin(operator.getUserId(), operator.getChannel())) {
            throw ErrorCode.PERMISSION_DENIED.toBizException();
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
        if (systemDO == null || !ValidStatus.VALID.toString().equals(systemDO.getStatus())) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的系统编号（不存在或未启用）：" + code);
        }
    }
}
