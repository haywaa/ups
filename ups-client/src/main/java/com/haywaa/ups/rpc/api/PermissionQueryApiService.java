package com.haywaa.ups.rpc.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.haywaa.ups.rpc.bean.DataResult;
import com.haywaa.ups.rpc.dto.UserPermissionDTO;
import com.haywaa.ups.rpc.dto.request.PermissionQueryReq;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-19 15:05
 */
@RequestMapping("/api/permission")
public interface PermissionQueryApiService {

    /**
     * 用户资料查询接口
     */
    @PostMapping("/query")
    DataResult<UserPermissionDTO> queryPermission(@RequestBody PermissionQueryReq req);
}
