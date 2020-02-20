package com.haywaa.ups.user;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.haywaa.ups.domain.entity.UserDO;
import com.haywaa.ups.user.bo.UserBO;
import com.haywaa.ups.user.bo.UserCheckBO;

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

    UserBO selectById(Long id, String channel);

    Long selectUserIdByThirdId(String channel, String channelUid);

    //Long selectUserIdByCode(String channel, String usercode);
    //
    //Long selectUserIdByThirdId(String channel, String thirdId);

    void checkChannelUser(List<UserCheckBO> userList);
}
