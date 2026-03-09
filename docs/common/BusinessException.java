package com.insurance.common.exception;

import lombok.Getter;

/**
 * 业务异常
 * 用于在业务逻辑中抛出可预期的异常
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    /**
     * 快速创建业务异常
     */
    public static BusinessException of(String message) {
        return new BusinessException(message);
    }

    public static BusinessException of(Integer code, String message) {
        return new BusinessException(code, message);
    }
}
