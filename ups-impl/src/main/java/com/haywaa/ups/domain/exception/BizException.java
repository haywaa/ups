package com.haywaa.ups.domain.exception;

/**
 * @description
 * @author: haywaa
 * @create: 2019-11-26 14:40
 */
public class BizException extends RuntimeException {

    private int errorCode;

    public BizException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
