package com.ghtk.social_network.application.config.logic;

import com.ghtk.social_network.application.config.StrongPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    String regexStrongPassword = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()]).{8,}$";
    return value.matches(regexStrongPassword);
  }
}
