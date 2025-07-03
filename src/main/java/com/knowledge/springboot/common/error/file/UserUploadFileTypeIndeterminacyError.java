package com.knowledge.springboot.common.error.file;

import com.knowledge.springboot.common.ErrorCode;
import com.knowledge.springboot.common.error.HavingErrorCodeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jxd
 * {@code @date} 2024/8/31 15:10
 */
@Getter
@Setter
@AllArgsConstructor
@SuppressWarnings("unused")
public class UserUploadFileTypeIndeterminacyError extends Exception implements HavingErrorCodeException {
    public ErrorCode errorCode;
    public String userHint;
    public String errorMessage;

    @SuppressWarnings("unused")
    public UserUploadFileTypeIndeterminacyError() {
        this.errorCode = ErrorCode.USER_UPLOAD_FILE_TYPE_INDETERMINACY;
        this.userHint = this.errorCode.getUserHint();
        this.errorMessage = this.errorCode.getErrorMessage();
    }

    public UserUploadFileTypeIndeterminacyError(final String userHint, final String errorMessage) {
        this.errorCode = ErrorCode.USER_UPLOAD_FILE_TYPE_INDETERMINACY;
        this.userHint = userHint;
        this.errorMessage = errorMessage;
    }
}
