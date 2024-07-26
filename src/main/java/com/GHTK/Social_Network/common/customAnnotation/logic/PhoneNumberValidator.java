package com.GHTK.Social_Network.common.customAnnotation.logic;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidPhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final String PHONE_REGEX = "^(84|0[3|5|7|8|9])([0-9]{8})$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return value.matches(PHONE_REGEX);
    }
}