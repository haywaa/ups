package com.haywaa.ups.rest.vo;

import java.time.LocalDateTime;

import com.haywaa.ups.domain.constants.ValidStatus;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-12-02 16:01
 */
@Data
public class SystemVO {
    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 状态 {@link ValidStatus#getCode()}
     */
    private Integer status;

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
