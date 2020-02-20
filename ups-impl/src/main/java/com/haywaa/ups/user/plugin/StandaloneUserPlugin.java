package com.haywaa.ups.user.plugin;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.haywaa.ups.dao.UserDAO;
import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.entity.UserDO;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.user.bo.UserCheckBO;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-19 13:34
 */
@Service
public class StandaloneUserPlugin extends AbstractUserPlugin {

    @Autowired
    private UserDAO userDAO;

    @Override
    protected String getChannel() {
        return "local";
    }

    @Override
    public Long addOrUpdate(UserDO userDO) {
        if (userDO.getUsercode() == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "用户号必填");
        }

        if (userDO.getName() == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "用户名必填");
        }

        if (userDO.getStatus() == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "用户状态必填");
        }

        userDAO.addOrUpdate(userDO);
        return userDO.getId();
    }

    @Override
    public UserDO selectById(Long id) {
        if (id == null) {
            return null;
        }

        return userDAO.selectById(id);
    }

    //@Override
    //public Long selectUserIdByCode(String channel, String usercode) {
    //    return userDAO.selectUserIdByCode(channel, usercode);
    //}
    //
    //@Override
    //public Long selectUserIdByThirdId(String channel, String thirdId) {
    //    return userDAO.selectUserIdByThirdId(channel, thirdId);
    //}


    @Override
    public void checkChannelUser(List<UserCheckBO> userList) {
        List<Long> userIds = userList.stream().map(UserCheckBO::getUserId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        List<UserDO> userDOList = userDAO.selectByIds(userIds);
        if (CollectionUtils.isEmpty(userDOList)) {
            throw ErrorCode.INVALID_PARAM.toBizException("无效的用户ID：" + StringUtils.join(userIds, ","));
        }

        Set<Long> dbUserIdSet = userDOList.stream().map(UserDO::getId).collect(Collectors.toSet());
        for (UserCheckBO userCheckBO : userList) {
            if (userCheckBO.getUserId() == null) {
                throw ErrorCode.INVALID_PARAM.toBizException("用户ID不能为空");
            }

            if (!dbUserIdSet.contains(userCheckBO.getUserId())) {
                throw ErrorCode.INVALID_PARAM.toBizException("用户ID不归属于渠道：" + userCheckBO.getChannel());
            }
        }
    }

    @Override
    public Long selectUserIdByThirdId(String channel, String thirdUid) {
        return userDAO.selectUserIdByThirdId(channel, thirdUid);
    }
}
