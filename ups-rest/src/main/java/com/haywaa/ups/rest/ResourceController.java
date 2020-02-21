//package com.haywaa.ups.rest;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.haywaa.ups.permission.bo.OperatorInfo;
//import com.haywaa.ups.domain.constants.ErrorCode;
//import com.haywaa.ups.domain.constants.ValidStatus;
//import com.haywaa.ups.domain.entity.ResourceDO;
//import com.haywaa.ups.domain.exception.BizException;
//import com.haywaa.ups.rest.convert.ResourceConvert;
//import com.haywaa.ups.rest.param.ResourceParam;
//import com.haywaa.ups.rest.vo.ResourceVO;
//import com.haywaa.ups.rest.web.HttpResult;
//import com.haywaa.ups.rest.web.OperateContext;
//import com.haywaa.ups.permission.service.ResourceService;
//
///**
// * @description
// * @author: haywaa
// * @create: 2019-11-25 17:46
// */
//@RestController
//@RequestMapping("/resource")
//public class ResourceController {
//
//    @Autowired
//    private ResourceService resourceService;
//
//    @PostMapping()
//    public HttpResult insert(ResourceParam params) {
//        OperatorInfo operatorInfo = OperateContext.getNotNull();
//
//        params.setId(null);
//        if (StringUtils.isBlank(params.getCode())) {
//            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：编号不能为空");
//        }
//
//        if (StringUtils.isBlank(params.getName())) {
//            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：名称不能为空");
//        }
//
//        if (params.getStatus() == null) {
//            params.setStatus(ValidStatus.VALID.toString());
//        } else {
//            if (ValidStatus.codeOf(params.getStatus()) == null) {
//                throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：状态" + params.getStatus());
//            }
//        }
//
//        ResourceDO resourceDO = params.convertToDO();
//        Integer id = resourceService.insert(resourceDO, operatorInfo);
//        return HttpResult.Success(id);
//    }
//
//    @PostMapping("/{id}")
//    public HttpResult update(@PathVariable(name = "id") Integer id, ResourceParam params) {
//        OperatorInfo operatorInfo = OperateContext.getNotNull();
//        params.setId(id);
//        params.setCode(null);
//        params.setSystemCode(null);
//        params.setModuleCode(null);
//
//        if (params.getStatus() != null && ValidStatus.codeOf(params.getStatus()) == null) {
//            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "无效的参数：状态" + params.getStatus());
//        }
//
//        ResourceDO resourceDO = params.convertToDO();
//        resourceService.update(resourceDO, operatorInfo);
//        return HttpResult.Success();
//    }
//
//    @GetMapping(params = {"method=all"})
//    public HttpResult<List<ResourceVO>> tree(String systemCode, String status) {
//        if (systemCode == null) {
//            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请求参数异常：缺少系统编号");
//        }
//
//        List<ResourceDO> resourceDoList = resourceService.selectAll(systemCode, status);
//        if (CollectionUtils.isEmpty(resourceDoList)) {
//            return HttpResult.Success(new ArrayList<>());
//        }
//
//        List<ResourceVO> resourceVoList = new ArrayList<>(resourceDoList.size());
//        for (ResourceDO resourceDO : resourceDoList) {
//            resourceVoList.add(ResourceConvert.convertDO2VO(resourceDO));
//        }
//        return HttpResult.Success(resourceVoList);
//    }
//
//    //@GetMapping()
//    //public HttpResult<List<ResourceVO>> list(ResourceQuery query) {
//    //    if (query.getSystemCode() == null) {
//    //        throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请求参数异常：缺少系统编号");
//    //    }
//    //
//    //    return HttpResult.Success();
//    //}
//}
