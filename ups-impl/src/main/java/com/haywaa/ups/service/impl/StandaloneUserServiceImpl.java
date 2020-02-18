package com.haywaa.ups.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haywaa.ups.dao.UserDAO;
import com.haywaa.ups.domain.constants.ErrorCode;
import com.haywaa.ups.domain.entity.UserDO;
import com.haywaa.ups.domain.exception.BizException;
import com.haywaa.ups.service.UserService;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-29 16:55
 */
@Service
public class StandaloneUserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

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

        if (userDO.getChannel() == null) {
            throw new BizException(ErrorCode.INVALID_PARAM.getErrorNo(), "来源渠道必填");
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

    @Override
    public Long selectUserIdByCode(String channel, String usercode) {
        return userDAO.selectUserIdByCode(channel, usercode);
    }

    @Override
    public Long selectUserIdByThirdId(String channel, String thirdId) {
        return userDAO.selectUserIdByThirdId(channel, thirdId);
    }
}
