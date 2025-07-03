package com.knowledge.springboot.common.util;

import javax.validation.*;
import java.util.Set;

/**
 * 校验入参工具
 *
 * @author jxd
 * {@code @date} 2023.04.04
 */
public class ValidatorUtils {

    private static final Validator validator;

    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    public static void validateEntity(Object object, Class<?>... groups) throws ValidationException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            constraintViolations.forEach(o -> {
                throw new ValidationException(o.getMessage());
            });
        }
    }
}
