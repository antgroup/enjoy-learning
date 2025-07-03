package com.knowledge.springboot.common;

import com.knowledge.springboot.common.error.BusinessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ValidationException;

/**
 * 全局异常处理器
 *
 * @author jxd
 * {@code @date} 2023/4/4 11:29
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ValidationException.class)
    @ResponseBody
    public Object exceptionHandler() {
        return Statusful.error(new BusinessException());
    }
}
