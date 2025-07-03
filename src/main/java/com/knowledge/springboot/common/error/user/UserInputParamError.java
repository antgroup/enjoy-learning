package com.knowledge.springboot.common.error.user;

import com.knowledge.springboot.common.ErrorCode;
import com.knowledge.springboot.common.error.HavingErrorCodeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jxd
 * {@code @date} 2024/8/31 19:29
 */
@Getter
@Setter
@AllArgsConstructor
@SuppressWarnings("unused")
public class UserInputParamError extends Exception implements HavingErrorCodeException {
    public ErrorCode errorCode;
    public String userHint;
    public String errorMessage;

    public UserInputParamError() {
        this.errorCode = ErrorCode.USER_INPUT_PARAM_ERROR;
        this.userHint = this.errorCode.getUserHint();
        this.errorMessage = this.errorCode.getErrorMessage();
    }

    public UserInputParamError(String userHint, String errorMessage) {
        this.errorCode = ErrorCode.USER_INPUT_PARAM_ERROR;
        this.userHint = userHint;
        this.errorMessage = errorMessage;
    }

    public UserInputParamError(String userHint) {
        this.errorCode = ErrorCode.USER_INPUT_PARAM_ERROR;
        this.userHint = userHint;
        this.errorMessage = ErrorCode.USER_INPUT_PARAM_ERROR.getErrorMessage();
    }
}
