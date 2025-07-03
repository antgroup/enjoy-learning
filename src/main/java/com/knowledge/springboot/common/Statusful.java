package com.knowledge.springboot.common;

import com.knowledge.springboot.common.error.HavingErrorCodeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Optional;

public class Statusful<T> {
    public Statusful() {
    }

    public static <T> ResponseEntity<ResultVo<T>> ok(T data) {
        return ResponseEntity.status(HttpStatus.OK).body(ResultVo.okWithData(data));
    }

    public static <T> ResponseEntity<ResultVo<T>> ok(T data, String userHint) {
        return ResponseEntity.status(HttpStatus.OK).body(ResultVo.ok(userHint, data));
    }

    public static ResponseEntity<ResultVo<Object>> ok(String userHint) {
        return ResponseEntity.status(HttpStatus.OK).body(ResultVo.ok(userHint, null));
    }

    public static ResponseEntity<ResultVo<Object>> error(ErrorCode errorCode) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResultVo.error(errorCode));
    }

    public static ResponseEntity<ResultVo<Object>> error(String code, String userHint, String errorMessage) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResultVo.error(code, userHint,
                errorMessage));
    }

    public static ResponseEntity<ResultVo<Object>> error(HttpStatus httpStatusCode, String code, String userHint,
                                                         String errorMessage) {
        return ResponseEntity.status(httpStatusCode).body(ResultVo.error(code, userHint, errorMessage));
    }

    public static ResponseEntity<Object> error(HavingErrorCodeException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        String userHint;
        String errorMessage;
        if (Objects.nonNull(errorCode)) {
            userHint = StringUtils.isNotBlank(exception.getUserHint()) ? exception.getUserHint() :
                    errorCode.getUserHint();
            errorMessage = StringUtils.isNotBlank(exception.getErrorMessage()) ? exception.getErrorMessage() :
                    errorCode.getErrorMessage();
        } else {
            userHint = exception.getUserHint();
            errorMessage = exception.getErrorMessage();
        }

        return ResponseEntity.of(Optional.of(ResultVo.error(errorCode.getCode(), userHint, errorMessage)));
    }
}
