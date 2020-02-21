package com.haywaa.ups.web.vo;

import java.time.LocalDateTime;

import com.haywaa.ups.domain.constants.ValidStatus;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-26 11:04
 */
@Data
public class RoleVO {

    private Integer id;

    private String code;

    private String name;

    private String systemCode;

    private String moduleCode;

    private String type;

    /**
     * @see ValidStatus#toString()
     */
    private String status;

    private String comment;

    private String creator;

    private String modifier;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime updatedTime;
}
