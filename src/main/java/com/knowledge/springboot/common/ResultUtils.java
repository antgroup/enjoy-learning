package com.knowledge.springboot.common;

/**
 * 响应结果工具类
 */
public class ResultUtils {

    /**
     * 成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 通用响应对象
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(ErrorCode.OK.getCode(), ErrorCode.OK.getUserHint(), null, data);
    }

    /**
     * 错误响应
     * @param errorCode 错误码
     * @param <T> 数据类型
     * @return 通用响应对象
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode.getCode(), errorCode.getUserHint(), errorCode.getErrorMessage(), null);
    }

    /**
     * 带自定义错误信息的错误响应
     * @param errorCode 错误码
     * @param message 自定义错误信息
     * @param <T> 数据类型
     * @return 通用响应对象
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), errorCode.getUserHint(), message, null);
    }

    /**
     * 带数据的错误响应
     * @param errorCode 错误码
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 通用响应对象
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, T data) {
        return new BaseResponse<>(errorCode.getCode(), errorCode.getUserHint(), errorCode.getErrorMessage(), data);
    }
} 