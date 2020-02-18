package com.haywaa.ups.domain.entity;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 17:38
 */
@Data
public class UserRoleDO extends BaseDO<Integer> {

    private Long userId;

    private Integer roleId;
}
