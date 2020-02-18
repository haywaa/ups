package com.haywaa.ups.domain.cache;

import java.util.List;

import com.haywaa.ups.domain.entity.RoleDO;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-28 18:19
 */
@Data
public class RoleCache {

    private volatile RoleDO role;

    private List<Integer> resourceList;
}
