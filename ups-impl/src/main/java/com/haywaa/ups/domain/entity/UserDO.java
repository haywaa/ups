package com.haywaa.ups.domain.entity;

import com.haywaa.ups.domain.constants.ValidStatus;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 17:36
 */
@Data
public class UserDO extends BaseDO<Long> {

    /**
     * 用户号
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
     * {@link ValidStatus#getCode()}
     * 状态 1 有效 0 禁用
     */
    Integer status;

    /**
     * 手机号
     */
    String mobile;
}
