package com.haywaa.ups.permission.service;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-21 16:10
 */
public interface OperateAuthCheckService {

    boolean isUpsAdmin(Long userId, String channel);

    boolean isSystemAdmin(Long userId, String channel, String systemCode);
}
