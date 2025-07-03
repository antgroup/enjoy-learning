package com.knowledge.springboot.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用响应类
 * @param <T> 响应数据类型
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = -983735864726303923L;

    /**
     * 状态码
     */
    private String code;

    /**
     * 用户提示信息
     */
    private String userHint;

    /**
     * 错误信息（仅在错误情况下使用）
     */
    private String errorMessage;

    /**
     * 响应数据
     */
    private T data;

    public BaseResponse() {
    }

    public BaseResponse(String code, String userHint, String errorMessage, T data) {
        this.code = code;
        this.userHint = userHint;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(ErrorCode.OK.getCode(), ErrorCode.OK.getUserHint(), null, data);
    }

    /**
     * 错误响应
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode.getCode(), errorCode.getUserHint(), errorCode.getErrorMessage(), null);
    }

    /**
     * 带自定义错误信息的错误响应
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String errorMessage) {
        return new BaseResponse<>(errorCode.getCode(), errorCode.getUserHint(), errorMessage, null);
    }
} 