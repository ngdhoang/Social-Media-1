package com.GHTK.Social_Network.application.customAnnotation.logic;

import com.GHTK.Social_Network.application.customAnnotation.config.ValidOrderBy;
import com.GHTK.Social_Network.application.customAnnotation.Enum.EOrderBy;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OrderByValidator implements ConstraintValidator<ValidOrderBy, String> {

    @Override
    public void initialize(ValidOrderBy constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }

        try {
            EOrderBy.valueOf(s.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
