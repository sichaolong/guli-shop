package henu.soft.xiaosi.product.util;

import henu.soft.xiaosi.product.entity.BrandEntity;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 本地方法手动测试valid验证
 */

public class ValidateUtil {
    private static Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();

    public static void beanValidate(Object obj) throws Exception {
        Map<String, String> validatedMsg = new HashMap<>();
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);

        for (ConstraintViolation<Object> c : constraintViolations) {
            validatedMsg.put(c.getPropertyPath().toString(), c.getMessage());
        }

        if (CollectionUtils.isNotEmpty(constraintViolations)) {
            throw new Exception(String.valueOf(validatedMsg));
        }

    }

    public static void main(String[] args) throws Exception {
        beanValidate(new BrandEntity());
    }

}
