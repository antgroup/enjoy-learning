package com.knowledge.springboot.common.error.db;

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
public class DbStoreFormatError extends Exception implements HavingErrorCodeException {
    public ErrorCode errorCode;
    public String userHint;
    public String errorMessage;

    @SuppressWarnings("unused")
    public DbStoreFormatError() {
        this.errorCode = ErrorCode.SYSTEM_DB_STORAGE_FORMAT_ERROR;
        this.userHint = ErrorCode.SYSTEM_DB_STORAGE_FORMAT_ERROR.getUserHint();
        this.errorMessage = ErrorCode.SYSTEM_DB_STORAGE_FORMAT_ERROR.getErrorMessage();
    }

    public DbStoreFormatError(String userHint, String errorMessage) {
        this.errorCode = ErrorCode.SYSTEM_DB_STORAGE_FORMAT_ERROR;
        this.userHint = userHint;
        this.errorMessage = errorMessage;
    }
}
