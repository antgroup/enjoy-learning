package com.knowledge.springboot.common;

import lombok.Getter;

import java.io.Serializable;

/**
 * 错误码
 *
 * @author jxd
 * {@code @date} 2024/08/30 22:04
 */
@Getter
public enum ErrorCode implements Serializable {
    OK("00000", "SUCCESS", ""),
    // 一级宏观错误码,用户错误
    USER_ERROR("A0001", "USER ERROR", "USER BEHAVIOR ERROR"),
    // 二级宏观错误码,用户注册错误
    USER_REGIST_ERROR("A0100", "USER REGIST ERROR", "USER REGIST ERROR"),
    // 二级宏观错误码,用户登录异常
    USER_LOGIN_ERROR("A0200", "USER LOGIN ERROR", "USER LOGIN ERROR"),
    USER_NO_AUTHORIZATION("A0201", "USER NO AUTHORIZATION", "USER NO AUTHORIZATION"),
    USER_AUTHORIZATION_INFORMATION_ABNORMAL("A0202", "USER AUTHORIZATION INFORMATION ABNORMAL", "USER AUTHORIZATION " +
            "INFORMATION IS ABNORMAL"),
    // 二级宏观错误码,用户访问权限异常
    USER_PERMISSION_ERROR("A0300", "USER PERMISSION ERROR", "USER PERMISSION ERROR"),
    // 二级宏观错误码,用户请求参数异常
    USER_INPUT_PARAM_ERROR("A0400", "USER INPUT PARAM ERROR", "USER INPUT PARAM ERROR"),
    // 二级宏观错误吗,用户上传文件异常
    USER_UPLOAD_FILE_ERROR("A0700", "USER UPLOAD FILE ERROR", "USER UPLOAD FILE ERROR"),
    // 用户上传的文件内容有问题
    USER_UPLOAD_FILE_CONTENT_ERROR("A0701", "USER UPLOAD FILE CONTENT ERROR", "USER UPLOAD FILE CONTENT ERROR"),
    // 上传的文件的文件类型不知道
    // @formatter:off
    USER_UPLOAD_FILE_TYPE_INDETERMINACY("A0702", "USER UPLOAD FILE TYPE INDETERMINACY ERROR", "USER UPLOAD FILE TYPE INDETERMINACY ERROR"),
    // @formatter:on

    // 一级宏观错误码,系统执行出错
    SYSTEM_ERROR("B0001", "SYSTEM ERROR", "SYSTEM ERROR"),
    // 二级宏观错误码,数据库异常
    SYSTEM_DB_ERROR("B0400", "SYSTEM DB ERROR", "SYSTEM DB ERROR"),
    // 数据库存储的数据格式不对
    SYSTEM_DB_STORAGE_FORMAT_ERROR("B0401", "SYSTEM DB STORAGE FORMAT ERROR", "SYSTEM DB STORAGE FORMAT ERROR"),

    // 一级宏观错误码,调用三方服务出错
    THREE_PARTY_ERROR("C0001", "SYSTEM FAILED TO INVOKE THE THREE-PARTY SERVICE", "THREE PARTY ERROR"),
    // 二级宏观错误码,中间件服务出错
    THREE_PARTY_MIDDLEWARE_ERROR("C0100", "SYSTEM COMPONENT ERROR", "THREE PARTY MIDDLEWARE ERROR"),
    // 二级宏观错误码,oss 服务出错
    THREE_PARTY_OSS_ERROR("C0600", "OSS COMPONENT ERROR", "OSS COMPONENT ERROR");

    ErrorCode(String code, String userHint, String errorMessage) {
        this.code = code;
        this.userHint = userHint;
        this.errorMessage = errorMessage;
    }

    /**
     * 错误编码
     */
    private final String code;
    /**
     * 用户提示信息
     */
    private final String userHint;
    /**
     * 错误信息，便于排查问题，不要暴露敏感数据信息
     */
    private final String errorMessage;
}
