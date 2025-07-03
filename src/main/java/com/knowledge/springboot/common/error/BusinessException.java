package com.knowledge.springboot.common.error;

import com.knowledge.springboot.common.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jxd
 * {@code @date} 2024/8/31 12:46
 */
@Getter
@Setter
@AllArgsConstructor
@SuppressWarnings("unused")
public class BusinessException extends Exception implements HavingErrorCodeException {
    public ErrorCode errorCode;
    public String userHint;
    public String errorMessage;

    public BusinessException() {
        this.errorCode = ErrorCode.SYSTEM_ERROR;
        this.userHint = ErrorCode.SYSTEM_ERROR.getUserHint();
        this.errorMessage = ErrorCode.SYSTEM_ERROR.getErrorMessage();
    }

    public BusinessException(final ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.userHint = errorCode.getUserHint();
        this.errorMessage = errorCode.getErrorMessage();
    }

    public BusinessException(String userHint, String errorMessage) {
        this.errorCode = ErrorCode.SYSTEM_ERROR;
        this.userHint = userHint;
        this.errorMessage = errorMessage;
    }
}
