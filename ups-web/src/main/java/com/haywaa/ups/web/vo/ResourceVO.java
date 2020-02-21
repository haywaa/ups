package com.haywaa.ups.web.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-26 11:27
 */
@Data
public class ResourceVO {

    private Integer id;

    private String code;

    private String parentCode;

    private String name;

    private String type;

    private String items;

    private String systemCode;

    private String moduleCode;

    private String status;

    /**
     * 排序码
     */
    private Integer sortNum;

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
