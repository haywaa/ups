package com.haywaa.ups.domain.bo;

import lombok.Builder;
import lombok.Getter;

/**
 * 用户信息
 */
@Getter
@Builder
public class OperatorInfo {

    private Long userId;

    private String operatorCode;
}