package com.GHTK.Social_Network.application.customAnnotation.logic;

import com.GHTK.Social_Network.application.customAnnotation.config.ValidSortBy;
import com.GHTK.Social_Network.application.customAnnotation.Enum.ESortBy;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SortByValidator implements ConstraintValidator<ValidSortBy, String> {
    @Override
    public void initialize(ValidSortBy constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        try {
            ESortBy.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
