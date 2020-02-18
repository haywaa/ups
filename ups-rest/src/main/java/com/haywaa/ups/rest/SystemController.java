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
import com.haywaa.ups.domain.entity.SystemDO;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.rest.convert.SystemConvert;
import com.haywaa.ups.rest.vo.SystemVO;
import com.haywaa.ups.rest.web.HttpResult;
import com.haywaa.ups.rest.web.OperateContext;
import com.haywaa.ups.rest.web.PageData;
import com.haywaa.ups.service.SystemService;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 17:46
 */
@RestController
@RequestMapping("/system")
public class SystemController {

    @Autowired
    private SystemService systemService;

    @PostMapping()
    public HttpResult insert(String code, String name, Integer status) {
        OperatorInfo operatorInfo = OperateContext.getNotNull();

        if (StringUtils.isBlank(code)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：编号不能为空");
        }

        if (StringUtils.isBlank(name)) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：名称不能为空");
        }

        if (status != null && ValidStatus.codeOf(status) == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：状态" + status);
        }

        SystemDO systemDO = new SystemDO();
        systemDO.setCode(code);
        systemDO.setName(name);
        systemDO.setStatus(status == null ? ValidStatus.VALID.getCode() : status);

        Integer id = systemService.insert(systemDO, operatorInfo);
        return HttpResult.Success(id);
    }

    @PostMapping("/{id}")
    public HttpResult update(
            @PathVariable(name = "id") Integer id, String name, Integer status) {
        OperatorInfo operatorInfo = OperateContext.getNotNull();

        if (name == null && status == null) {
            return HttpResult.Success();
        }

        if (status != null && ValidStatus.codeOf(status) == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：状态" + status);
        }

        SystemDO systemDO = new SystemDO();
        systemDO.setId(id);
        systemDO.setName(name);
        systemDO.setStatus(status);

        systemService.update(systemDO, operatorInfo);
        return HttpResult.Success();
    }

    @GetMapping(params = {"method=all"})
    public HttpResult<List> selectAll(Integer status) {
        List<SystemDO> systemDoList = systemService.selectAll(status);
        if (CollectionUtils.isEmpty(systemDoList)) {
            return HttpResult.Success(new ArrayList<>());
        }

        List<SystemVO> systemVoList = new ArrayList<>(systemDoList.size());
        for (SystemDO systemDO : systemDoList) {
            systemVoList.add(SystemConvert.convertDO2VO(systemDO));
        }
        return HttpResult.Success(systemVoList);
    }

    @GetMapping(params = {"method=page"})
    public HttpResult<PageData> selectPage() {
        return HttpResult.Success();
    }
}
