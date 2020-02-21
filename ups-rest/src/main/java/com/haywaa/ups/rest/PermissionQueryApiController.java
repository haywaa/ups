package com.haywaa.ups.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.haywaa.ups.permission.service.PermissionQueryService;
import com.haywaa.ups.rpc.api.PermissionQueryApiService;
import com.haywaa.ups.rpc.bean.DataResult;
import com.haywaa.ups.rpc.dto.UserPermissionDTO;
import com.haywaa.ups.rpc.dto.request.PermissionQueryReq;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-21 21:22
 */
@RestController
public class PermissionQueryApiController implements PermissionQueryApiService {

    @Autowired
    private PermissionQueryService permissionQueryService;

    @Override
    public DataResult<UserPermissionDTO> queryPermission(@RequestBody PermissionQueryReq req) {
        UserPermissionDTO permissionDTO = permissionQueryService.queryUserPermission(req.getSystemCode(), req.getChannel(), req.getThirdUid());
        return DataResult.Success(permissionDTO);
    }
}
