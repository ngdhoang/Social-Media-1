package com.GHTK.Social_Network.application.customAnnotation.logic;

import com.GHTK.Social_Network.application.customAnnotation.config.ValidPhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String regexPhoneNumber = "/(84|0[3|5|7|8|9])+([0-9]{8})\\b/g";
        return value.matches(regexPhoneNumber);
    }
}