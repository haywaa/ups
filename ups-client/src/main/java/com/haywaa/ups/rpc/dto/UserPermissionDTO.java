package com.haywaa.ups.rpc.dto;

import java.util.List;

import com.haywaa.ups.rpc.enums.ResourceType;

import lombok.Data;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-21 21:35
 */
@Data
public class UserPermissionDTO {

    private List<RoleItem> roleList;

    private List<ResourceItem> resouceList;

    @Data
    public static class RoleItem {
        private Integer roleId;

        private String roleCode;

        private String relatedKey;
    }

    @Data
    public static class ResourceItem {
        /**
         * 编号
         * 不允许修改
         */
        private String code;

        /**
         * 资源类型 {@link ResourceType#toString()}
         * 不允许修改
         */
        private String type;

        /**
         * 资源项
         */
        private String items;

        /**
         * 拥有该资源权限的角色ID
         */
        private List<Integer> relatedRoleIds;
    }
}
