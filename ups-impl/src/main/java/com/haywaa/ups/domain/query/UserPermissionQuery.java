package com.haywaa.ups.domain.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-29 16:01
 */
@AllArgsConstructor
@Getter
public class UserPermissionQuery {

    @NonNull
    private String systemCode;

    @NonNull
    private Long userId;

    @NonNull
    private String channel;
}
