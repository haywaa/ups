package com.haywaa.ups.user.convert;

import com.haywaa.ups.domain.entity.UserDO;
import com.haywaa.ups.user.bo.UserBO;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-20 22:11
 */
public class UserConvert {

    public static UserBO convertDO2BO(UserDO userDO) {
        if (userDO == null) {
            return null;
        }
        UserBO userBO = new UserBO();
        userBO.setUsercode(userDO.getUsercode());
        userBO.setName(userDO.getName());
        userBO.setThirdId(userDO.getThirdId());
        userBO.setStatus(userDO.getStatus());
        userBO.setMobile(userDO.getMobile());
        userBO.setUserId(userDO.getId());
        return userBO;
    }
}
