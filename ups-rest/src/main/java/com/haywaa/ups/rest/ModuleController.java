package com.haywaa.ups.rest;

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

import com.haywaa.ups.domain.bo.OperatorInfo;
import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.constants.ValidStatus;
import com.haywaa.ups.domain.entity.ModuleDO;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.rest.convert.ModuleConvert;
import com.haywaa.ups.rest.vo.ModuleVO;
import com.haywaa.ups.rest.web.HttpResult;
import com.haywaa.ups.rest.web.OperateContext;
import com.haywaa.ups.rest.web.PageData;
import com.haywaa.ups.service.ModuleService;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 18:50
 */
@RestController
@RequestMapping("/module")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @PostMapping()
    public HttpResult insert(String code, String name, String systemCode, Integer status) {
        OperatorInfo operatorInfo = OperateContext.getNotNull();

        if (StringUtils.isBlank(code)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：编号不能为空");
        }

        if (StringUtils.isBlank(name)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：名称不能为空");
        }

        if (StringUtils.isBlank(systemCode)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：系统编号不能为空");
        }

        if (status != null && ValidStatus.codeOf(status) == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：状态" + status);
        }

        ModuleDO moduleDO = new ModuleDO();
        moduleDO.setCode(code);
        moduleDO.setName(name);
        moduleDO.setSystemCode(systemCode);
        moduleDO.setStatus(status == null ? ValidStatus.VALID.getCode() : status);

        Integer id = moduleService.insert(moduleDO, operatorInfo);
        return HttpResult.Success(id);
    }

    @PostMapping("/{id}")
    public HttpResult update(
            @PathVariable(name = "id") Integer id, String name, Integer status) {
        OperatorInfo operatorInfo = OperateContext.getNotNull();

        if (StringUtils.isEmpty(name) && status == null) {
            return HttpResult.Success(null);
        }

        if (status != null && ValidStatus.codeOf(status) == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：状态" + status);
        }

        ModuleDO moduleDO = new ModuleDO();
        moduleDO.setId(id);
        moduleDO.setName(name);
        moduleDO.setStatus(status);

        moduleService.update(moduleDO, operatorInfo);
        return HttpResult.Success(null);
    }

    @GetMapping(params = {"method=all"})
    public HttpResult<List> selectAll(String systemCode, Integer status) {
        if (StringUtils.isEmpty(systemCode)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "系统编号必填");
        }

        List<ModuleDO> moduleDoList = moduleService.selectAll(systemCode, status);
        if (CollectionUtils.isEmpty(moduleDoList)) {
            return HttpResult.Success(new ArrayList<>());
        }

        List<ModuleVO> moduleVoList = new ArrayList<>(moduleDoList.size());
        for (ModuleDO moduleDO : moduleDoList) {
            moduleVoList.add(ModuleConvert.convertDO2VO(moduleDO));
        }
        return HttpResult.Success(moduleVoList);
    }

    @GetMapping(params = {"method=page"})
    public HttpResult<PageData> selectPage() {
        return HttpResult.Success();
    }
}
