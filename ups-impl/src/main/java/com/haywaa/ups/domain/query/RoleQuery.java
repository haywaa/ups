package com.haywaa.ups.domain.query;

import com.haywaa.ups.domain.constants.ValidStatus;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-12-02 17:13
 */
@Data
public class RoleQuery {

    String systemCode;

    String moduleCode;

    String code;

    String name;

    /**
     * {@link ValidStatus#getCode()}
     */
    Integer status;
}
