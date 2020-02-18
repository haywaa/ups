package com.haywaa.ups.domain.entity;

import lombok.Data;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-26 15:57
 */
@Data
public class SettingDO extends BaseDO<Integer> {

    private String code;

    private String key;

    private String value;
}
