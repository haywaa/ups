package com.haywaa.ups.rest.web;

import java.util.List;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-27 15:00
 */
@Data
public class PageData<T> {

    private Integer pageNo;

    private Integer pageSize;

    /**
     * 总行数
     */
    private Integer totalCount;

    private List<T> list;
}
