package com.haywaa.ups.rpc.dto.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-21 21:28
 */
@Data
public class PermissionQueryReq {

    @NotNull
    private String systemCode;

    @NotNull
    private String channel;

    @NotNull
    private String thirdUid;
}
