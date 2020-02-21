package com.haywaa.ups.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haywaa.ups.permission.bo.OperatorInfo;
import com.haywaa.ups.permission.bo.UserPermissionBO;
import com.haywaa.ups.domain.entity.UserDO;
import com.haywaa.ups.rest.param.UserParam;
import com.haywaa.ups.rest.web.HttpResult;
import com.haywaa.ups.rest.web.OperateContext;
import com.haywaa.ups.rpc.request.UserRoleDelReq;
import com.haywaa.ups.rpc.request.UserRoleGrantReq;
import com.haywaa.ups.permission.service.PermissionGrantService;
import com.haywaa.ups.permission.service.PermissionQueryService;
import com.haywaa.ups.user.UserService;

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
     * 用户同步接口, 将用户同步至本地
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
    public HttpResult queryPermission(String systemCode, String channel, String thirdUid) {
        UserPermissionBO permissionBO = permissionQueryService.queryUserPermission(systemCode, channel, thirdUid);
        return HttpResult.Success(permissionBO);
    }

    /**
     * 用户授权角色接口
     */
    @PostMapping(value = "/role", params = "method=add")
    public HttpResult grantPermission(UserRoleGrantReq grantReq) {
        OperatorInfo operatorInfo = OperateContext.getNotNull();

        permissionGrantService.grantPermission(grantReq, operatorInfo);
        return HttpResult.Success();
    }

    /**
     * 用户删除角色接口
     */
    @PostMapping(value = "/role", params = "method=remove")
    public HttpResult removeUserRole(UserRoleDelReq delReq) {
        OperatorInfo operatorInfo = OperateContext.getNotNull();

        permissionGrantService.removeRoles(delReq, operatorInfo);
        return HttpResult.Success();
    }
}
