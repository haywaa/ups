package com.haywaa.ups.web.param;

import com.haywaa.ups.domain.constants.ValidStatus;
import com.haywaa.ups.domain.entity.UserDO;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-12-02 15:07
 */
@Data
public class UserParam {

    /**
     * 用户号
     * 不存在可以同thirdId保持一致
     */
    String usercode;

    /**
     * 用户名
     */
    String name;

    /**
     * 用户来源渠道
     */
    String channel;

    /**
     * 渠道用户ID
     */
    String thirdId;

    /**
     * {@link ValidStatus#toString()}
     */
    String status;

    /**
     * 手机号
     */
    String mobile;

    public UserDO convertToDO() {
        UserDO userDO = new UserDO();
        userDO.setUsercode(this.getUsercode());
        userDO.setName(this.getName());
        userDO.setChannel(this.getChannel());
        userDO.setThirdId(this.getThirdId());
        userDO.setStatus(this.getStatus());
        userDO.setMobile(this.getMobile());
        return userDO;
    }
}
