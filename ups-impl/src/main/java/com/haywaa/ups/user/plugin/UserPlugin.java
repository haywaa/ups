package com.haywaa.ups.user.plugin;

import java.util.List;

import com.haywaa.ups.domain.entity.UserDO;
import com.haywaa.ups.user.bo.UserCheckBO;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-19 12:30
 */
public interface UserPlugin {

    /**
     * 新增或更新用户信息
     */
    Long addOrUpdate(UserDO userDO);

    UserDO selectById(Long id);

    void checkChannelUser(List<UserCheckBO> userList);

    Long selectUserIdByThirdId(String channel, String thirdUid);
}
