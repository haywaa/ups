package com.haywaa.ups.cooperate.bo;

import lombok.Data;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-19 17:47
 */
@Data
public class UserEventBO {

    private String channel;

    private Long userId;

    private String systemCode;
}
