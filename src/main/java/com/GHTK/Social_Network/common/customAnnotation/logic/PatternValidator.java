package com.GHTK.Social_Network.common.customAnnotation.logic;


import com.GHTK.Social_Network.common.customAnnotation.config.ValidPattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PatternValidator implements ConstraintValidator<ValidPattern, String> {

    private String regexPattern;
    private String errorMessage;

    @Override
    public void initialize(ValidPattern constraintAnnotation) {
        this.regexPattern = constraintAnnotation.value().getPattern();
        this.errorMessage = constraintAnnotation.value().getMessage();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        boolean isValid = value.matches(regexPattern);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
        }
        return isValid;
    }
}
