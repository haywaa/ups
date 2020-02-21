package com.haywaa.ups.rpc.dto.request;

import java.util.List;

import lombok.Data;

/**
 * @description
 * @author: qingye
 * @create: 2020-02-19 18:11
 */
@Data
public class UserRoleDelReq {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户来源渠道
     */
    private String channel;

    private String systemCode;

    private List<RoleItem> roleItems;

    @Data
    public static class RoleItem {

        private String roleCode;

        private String relatedKey;
    }
}
