package com.haywaa.ups.domain.entity;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 17:38
 */
@Data
public class RoleResourceDO extends BaseDO<Integer> {

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 资源ID
     */
    private Integer resourceId;
}
