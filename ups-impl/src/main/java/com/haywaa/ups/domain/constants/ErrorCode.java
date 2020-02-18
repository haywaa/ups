package com.haywaa.ups.domain.constants;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-26 14:47
 */
public enum ErrorCode {
    SUCCESS(0, null),

    INVALID_PARAM(7101, "无效的参数"),
    PERMISSION_DENIED(7400, "权限不足"),

    SERVICE_ERROR(7500, "服务异常");

    private final Integer errorNo;
    private final String errorMsg;

    ErrorCode(Integer errorNo, String errorMsg) {
        this.errorNo = errorNo;
        this.errorMsg = errorMsg;
    }

    public Integer getErrorNo() {
        return errorNo;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
