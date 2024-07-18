package com.GHTK.Social_Network.application.customAnnotation.logic;

import com.GHTK.Social_Network.application.customAnnotation.config.StrongPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    String regexStrongPassword = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()]).{8,}$";
    return value.matches(regexStrongPassword);
  }
}
