package com.knowledge.springboot.common.error.user;

import com.knowledge.springboot.common.ErrorCode;
import com.knowledge.springboot.common.error.HavingErrorCodeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户登录错误
 */
@Getter
@Setter
@AllArgsConstructor
@SuppressWarnings("unused")
public class UserLoginError extends Exception implements HavingErrorCodeException {
    public ErrorCode errorCode;
    public String userHint;
    public String errorMessage;

    public UserLoginError() {
        this.errorCode = ErrorCode.USER_LOGIN_ERROR;
        this.userHint = this.errorCode.getUserHint();
        this.errorMessage = this.errorCode.getErrorMessage();
    }

    public UserLoginError(String userHint, String errorMessage) {
        this.errorCode = ErrorCode.USER_LOGIN_ERROR;
        this.userHint = userHint;
        this.errorMessage = errorMessage;
    }

    public UserLoginError(String userHint) {
        this.errorCode = ErrorCode.USER_LOGIN_ERROR;
        this.userHint = userHint;
        this.errorMessage = ErrorCode.USER_LOGIN_ERROR.getErrorMessage();
    }
} 