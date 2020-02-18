package com.haywaa.ups.domain.entity;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-25 17:39
 */
@Data
public class BaseDO<T> {

    private T id;

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
