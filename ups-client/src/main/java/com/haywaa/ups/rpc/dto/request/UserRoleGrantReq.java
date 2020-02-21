package com.haywaa.ups.rpc.dto.request;

import java.util.List;

import lombok.Data;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-19 15:07
 */
@Data
public class UserRoleGrantReq {

    private String systemCode;

    private List<UserRoleItem> userRoleItemList;

    @Data
    public static class UserRoleItem {
        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 用户来源渠道
         */
        private String channel;

        /**
         * 角色Code
         */
        private String roleCode;

        /**
         * 角色关联业务ID，如门店管理员关联的门店ID
         */
        private String relatedKey;
    }
}
