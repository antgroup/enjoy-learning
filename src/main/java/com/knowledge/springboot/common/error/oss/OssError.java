package com.knowledge.springboot.common.error.oss;

import com.knowledge.springboot.common.ErrorCode;
import com.knowledge.springboot.common.error.HavingErrorCodeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据库存储格式错误
 *
 * @author jxd
 * {@code @date} 2024/8/31 8:16
 */
@Getter
@Setter
@AllArgsConstructor
@SuppressWarnings("unused")
public class OssError extends Exception implements HavingErrorCodeException {
    public ErrorCode errorCode;
    public String userHint;
    public String errorMessage;

    public OssError() {
        this.errorCode = ErrorCode.THREE_PARTY_OSS_ERROR;
        this.userHint = this.errorCode.getUserHint();
        this.errorMessage = this.errorCode.getErrorMessage();
    }

    public OssError(final String userHint, final String errorMessage) {
        this.errorCode = ErrorCode.THREE_PARTY_OSS_ERROR;
        this.userHint = userHint;
        this.errorMessage = errorMessage;
    }
}
