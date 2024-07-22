package com.GHTK.Social_Network.common.customAnnotation.logic;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String regexEmail = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return value.matches(regexEmail);
    }
}