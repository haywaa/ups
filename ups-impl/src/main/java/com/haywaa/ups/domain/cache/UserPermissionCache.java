package com.haywaa.ups.domain.cache;

import java.util.List;

import com.haywaa.ups.domain.entity.UserDO;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 18:16
 */
@Data
public class UserPermissionCache {

    private UserDO user;

    private List<Integer> roleIds;
}
