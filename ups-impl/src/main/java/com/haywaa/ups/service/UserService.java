package com.haywaa.ups.service;

import org.apache.ibatis.annotations.Param;

import com.haywaa.ups.domain.entity.UserDO;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-26 16:35
 */
public interface UserService {

    /**
     * 新增或更新用户信息
     */
    Long addOrUpdate(UserDO userDO);

    UserDO selectById(@Param("id") Long id);

    Long selectUserIdByCode(String channel, String usercode);

    Long selectUserIdByThirdId(String channel, String thirdId);
}
