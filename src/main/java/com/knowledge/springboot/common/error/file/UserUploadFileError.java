package com.knowledge.springboot.common.error.file;

import com.knowledge.springboot.common.ErrorCode;
import com.knowledge.springboot.common.error.HavingErrorCodeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jxd
 * {@code @date} 2024/8/31 12:57
 */
@Getter
@Setter
@AllArgsConstructor
@SuppressWarnings("unused")
public class UserUploadFileError extends Exception implements HavingErrorCodeException {
    public ErrorCode errorCode;
    public String userHint;
    public String errorMessage;

    public UserUploadFileError() {
        this.errorCode = ErrorCode.USER_UPLOAD_FILE_ERROR;
        this.userHint = this.errorCode.getUserHint();
        this.errorMessage = this.errorCode.getErrorMessage();
    }

    public UserUploadFileError(final String userHint, final String errorMessage) {
        this.errorCode = ErrorCode.USER_UPLOAD_FILE_ERROR;
        this.userHint = userHint;
        this.errorMessage = errorMessage;
    }
}
