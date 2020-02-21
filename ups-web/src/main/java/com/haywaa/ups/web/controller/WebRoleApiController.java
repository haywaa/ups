package com.haywaa.ups.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.constants.ValidStatus;
import com.haywaa.ups.domain.entity.RoleDO;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.domain.query.RoleQuery;
import com.haywaa.ups.permission.bo.OperatorInfo;
import com.haywaa.ups.permission.service.RoleService;
import com.haywaa.ups.utils.LangUtil;
import com.haywaa.ups.web.convert.RoleConvert;
import com.haywaa.ups.web.param.RoleParam;
import com.haywaa.ups.web.vo.RoleVO;
import com.haywaa.ups.web.web.HttpResult;
import com.haywaa.ups.web.web.OperateContext;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 17:46
 */
@RestController()
@RequestMapping("/upsbff/role")
public class WebRoleApiController {

    @Autowired
    private RoleService roleService;

    @PostMapping()
    public HttpResult insert(RoleParam params) {
        OperatorInfo operatorInfo = OperateContext.getNotNull();

        params.setId(null);
        if (StringUtils.isBlank(params.getCode())) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：编号不能为空");
        }

        if (StringUtils.isBlank(params.getName())) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：名称不能为空");
        }

        if (params.getSystemCode() == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：系统编号不能为空");
        }

        if (params.getStatus() == null) {
            params.setStatus(ValidStatus.VALID.toString());
        } else {
            if (ValidStatus.codeOf(params.getStatus()) == null) {
                throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：状态" + params.getStatus());
            }
        }

        RoleDO roleDO = params.convertToDO();
        Integer id = roleService.insert(roleDO, operatorInfo);
        return HttpResult.Success(id);
    }

    @PostMapping("/{id}")
    public HttpResult update(@PathVariable(name = "id") Integer id, RoleParam params) {
        OperatorInfo operatorInfo = OperateContext.getNotNull();

        params.setId(id);
        params.setCode(null);
        params.setSystemCode(null);
        params.setModuleCode(null);

        if (params.getStatus() != null && ValidStatus.codeOf(params.getStatus()) == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：状态" + params.getStatus());
        }

        RoleDO roleDO = params.convertToDO();
        roleService.update(roleDO, operatorInfo);
        return HttpResult.Success();
    }

    @GetMapping(params = "method=all")
    public HttpResult list(RoleQuery query) {
        if (query.getSystemCode() == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：系统编号必填");
        }

        List<RoleDO> roleDOList = roleService.selectAll(query);
        if (CollectionUtils.isEmpty(roleDOList)) {
            return HttpResult.Success(new ArrayList<>());
        }

        List<RoleVO> roleVOList = new ArrayList<>(roleDOList.size());
        for (RoleDO roleDO : roleDOList) {
            roleVOList.add(RoleConvert.convertDO2VO(roleDO));
        }
        return HttpResult.Success(roleVOList);
    }

    /**
     * 分配资源
     */
    @PostMapping("/resource")
    public HttpResult addRoleResourceIds(Integer roleId, String resourceIds) {
        OperatorInfo operatorInfo = OperateContext.getNotNull();

        String[] resourceIdArr = resourceIds.split(",");
        if (resourceIdArr == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交需要分配的角色");
        }

        List<Integer> resourceIdList = new ArrayList<>(resourceIdArr.length);
        for (String resourceIdStr : resourceIdArr) {
            Integer resourceId = LangUtil.parseInteger(resourceIdStr.trim(), null);
            if (resourceId == null) {
                continue;
            }
            resourceIdList.add(resourceId);
        }

        roleService.addRoleResourceIds(roleId, resourceIdList, operatorInfo);
        return HttpResult.Success();
    }
}
