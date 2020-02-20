package com.haywaa.ups.user.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.entity.UserDO;
import com.haywaa.ups.user.UserPluginManager;
import com.haywaa.ups.user.UserService;
import com.haywaa.ups.user.bo.UserBO;
import com.haywaa.ups.user.bo.UserCheckBO;
import com.haywaa.ups.user.convert.UserConvert;
import com.haywaa.ups.user.plugin.UserPlugin;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-19 13:27
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    public void checkChannelUser(List<UserCheckBO> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }

        if (userList.size() > 100) {
            throw ErrorCode.INVALID_PARAM.toBizException("当次操作用户数量不能大于100");
        }

        Map<String, List<UserCheckBO>> userMap = userList.stream().collect(Collectors.groupingBy(UserCheckBO::getChannel, Collectors.toList()));
        userMap.entrySet().forEach(entry -> {
            checkoutPlugin(entry.getKey()).checkChannelUser(entry.getValue());
        });
    }

    @Override
    public Long addOrUpdate(UserDO userDO) {
        return checkoutPlugin(userDO.getChannel()).addOrUpdate(userDO);
    }

    @Override
    public UserBO selectById(Long userId, String channel) {
        UserDO userDO = checkoutPlugin(channel).selectById(userId);
        UserBO userBO = UserConvert.convertDO2BO(userDO);
        if (userBO != null) {
            userBO.setChannel(channel);
        }
        return userBO;
    }

    @Override
    public Long selectUserIdByThirdId(String channel, String thirdUid) {
        return checkoutPlugin(channel).selectUserIdByThirdId(channel, thirdUid);
    }

    private UserPlugin checkoutPlugin(String channel) {
        if (channel == null) {
            throw ErrorCode.INVALID_PARAM.toBizException("无效的用户渠道:" + channel);
        }

        UserPlugin userPlugin = UserPluginManager.getPlugin(channel);
        if (userPlugin == null) {
            throw ErrorCode.INVALID_PARAM.toBizException("无效的用户渠道:" + channel);
        }
        return userPlugin;
    }
}
