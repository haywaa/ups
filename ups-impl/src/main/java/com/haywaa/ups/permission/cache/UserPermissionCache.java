package com.haywaa.ups.permission.cache;

import java.util.List;

import com.haywaa.ups.user.bo.UserBO;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 18:16
 */
@Data
public class UserPermissionCache {

    private UserBO user;

    private List<RoleItem> roleItems;

    @Data
    public static class RoleItem {
        private Integer roleId;
        private String relatedKey;
    }
}
