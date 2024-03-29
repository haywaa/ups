package com.haywaa.ups.user.bo;

import com.haywaa.ups.domain.constants.ValidStatus;

import lombok.Data;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-20 22:10
 */
@Data
public class UserBO {

    /**
     * 用户号
     */
    Long userId;

    /**
     * 用户所属渠道
     */
    String channel;

    /**
     * 用户号
     */
    String usercode;

    /**
     * 用户名
     */
    String name;

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
}
