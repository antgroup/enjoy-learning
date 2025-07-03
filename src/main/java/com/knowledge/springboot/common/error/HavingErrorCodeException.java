package com.knowledge.springboot.common.error;


import com.knowledge.springboot.common.ErrorCode;

/**
 * @author jxd
 * {@code @date} 2024/8/31 15:28
 */
public interface HavingErrorCodeException {
    ErrorCode getErrorCode();

    String getUserHint();

    String getErrorMessage();
}
