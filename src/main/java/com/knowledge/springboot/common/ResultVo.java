package com.knowledge.springboot.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author jxd
 * {@code @date} 2023/8/5 16:54
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")
public class ResultVo<T> implements Serializable {
    /**
     * 返回码
     */
    private String code;
    /**
     * 用户提示信息
     */
    private String userHint;
    /**
     * 错误消息
     */
    private String errorMessage;
    /**
     * 返回数据
     */
    private T data;

    public static ResultVo<Object> ok() {
        return ResultVo.builder()
                .code(ErrorCode.OK.getCode())
                .userHint(ErrorCode.OK.getUserHint())
                .build();
    }

    public static <T> ResultVo<T> okWithData(T data) {
        return ResultVo.<T>builder()
                .code(ErrorCode.OK.getCode())
                .data(data)
                .userHint(ErrorCode.OK.getUserHint())
                .build();
    }

    public static ResultVo<Object> okWithUserHint(String userHint) {
        return ResultVo.builder()
                .code(ErrorCode.OK.getCode())
                .userHint(userHint)
                .build();
    }

    public static <T> ResultVo<T> ok(String userHint, T data) {
        return ResultVo.<T>builder()
                .code(ErrorCode.OK.getCode())
                .userHint(userHint)
                .data(data)
                .build();
    }

    public static <T> ResultVo<Object> error() {
        return ResultVo.builder()
                .code(ErrorCode.SYSTEM_ERROR.getCode())
                .userHint(ErrorCode.SYSTEM_ERROR.getUserHint())
                .errorMessage(ErrorCode.SYSTEM_ERROR.getErrorMessage())
                .build();
    }

    public static ResultVo<Object> error(ErrorCode errorCode) {
        return ResultVo.builder()
                .code(errorCode.getCode())
                .userHint(errorCode.getUserHint())
                .errorMessage(errorCode.getErrorMessage())
                .build();
    }

    public static ResultVo<Object> error(String userHint) {
        return ResultVo.builder()
                .code(ErrorCode.SYSTEM_ERROR.getCode())
                .userHint(userHint == null || userHint.isEmpty() ? ErrorCode.SYSTEM_ERROR.getUserHint() : userHint)
                .build();
    }

    public static ResultVo<Object> error(String code, String userHint, String errorMessage) {
        return ResultVo.builder()
                .code(code)
                .userHint(userHint == null || userHint.isEmpty() ? ErrorCode.SYSTEM_ERROR.getUserHint() : userHint)
                .errorMessage(errorMessage)
                .build();
    }

}
