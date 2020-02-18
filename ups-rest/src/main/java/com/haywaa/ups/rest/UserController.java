package com.haywaa.ups.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haywaa.ups.domain.bo.OperatorInfo;
import com.haywaa.ups.domain.bo.UserPermissionBO;
import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.entity.UserDO;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.rest.param.UserParam;
import com.haywaa.ups.rest.web.HttpResult;
import com.haywaa.ups.rest.web.OperateContext;
import com.haywaa.ups.service.PermissionGrantService;
import com.haywaa.ups.service.PermissionQueryService;
import com.haywaa.ups.service.UserService;
import com.haywaa.ups.utils.LangUtil;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 17:46
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionGrantService permissionGrantService;

    @Autowired
    private PermissionQueryService permissionQueryService;

    /**
     * 用户同步接口
     */
    @PostMapping("/sync")
    public HttpResult sync(UserParam param) {
        UserDO userDO = param.convertToDO();
        Long id = userService.addOrUpdate(userDO);
        return HttpResult.Success(id);
    }

    /**
     * 用户同步接口
     */
    @GetMapping("/permission")
    public HttpResult queryPermission(String systemCode, String channel, String thridId, String usercode) {

        if (channel == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交用户来源渠道");
        }

        if (thridId == null && usercode == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交三方用户ID或用户号");
        }

        Long userId;
        if (usercode != null) {
            userId = userService.selectUserIdByCode(channel, usercode);
        } else {
            userId = userService.selectUserIdByThirdId(channel, thridId);
        }

        if (userId == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "用户不存在");
        }

        UserPermissionBO permissionBO = permissionQueryService.queryUserPermission(systemCode, userId);
        return HttpResult.Success(permissionBO);
    }

    /**
     * 用户授权角色接口
     * @Param roleIds 逗号分隔
     */
    @PostMapping(value = "/role", params = "method=add")
    public HttpResult grantPermission(Long userId, String systemCode, String roleIds) {
        OperatorInfo operatorInfo = OperateContext.getNotNull();

        String[] roleIdArr = roleIds.split(",");
        if (roleIdArr == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交需要分配的角色");
        }

        List<Integer> roleIdList = new ArrayList<>(roleIdArr.length);
        for (String roleIdStr : roleIdArr) {
            Integer roleId = LangUtil.parseInteger(roleIdStr.trim(), null);
            if (roleId == null) {
                continue;
            }
            roleIdList.add(roleId);
        }

        permissionGrantService.grantPermission(userId, systemCode, roleIdList, operatorInfo);
        return HttpResult.Success();
    }

    /**
     * 用户授权角色接口
     * @Param roleIds 逗号分隔
     */
    @PostMapping(value = "/role", params = "method=remove")
    public HttpResult removeUserRole(Long userId, String systemCode, String roleIds) {
        OperatorInfo operatorInfo = OperateContext.getNotNull();

        String[] roleIdArr = roleIds.split(",");
        if (roleIdArr == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "请提交需要移除的角色");
        }

        List<Integer> roleIdList = new ArrayList<>(roleIdArr.length);
        for (String roleIdStr : roleIdArr) {
            Integer roleId = LangUtil.parseInteger(roleIdStr.trim(), null);
            if (roleId == null) {
                continue;
            }
            roleIdList.add(roleId);
        }

        permissionGrantService.removeRoles(userId, systemCode, roleIdList, operatorInfo);
        return HttpResult.Success();
    }
}
