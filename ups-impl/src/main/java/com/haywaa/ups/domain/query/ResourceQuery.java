package com.haywaa.ups.domain.query;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-26 11:32
 */
@Data
public class ResourceQuery {
    /**
     * 系统编码
     */
    private String systemCode;

    /**
     * 业务模块编码
     */
    private String moduleCode;

    /**
     * 资源编码
     */
    private String parentCode;


    /**
     * 资源编码
     */
    private String code;

    /**
     * 资源名称，左右模糊查询
     */
    private String name;

    /**
     * 状态
     */
    private String status;

}
