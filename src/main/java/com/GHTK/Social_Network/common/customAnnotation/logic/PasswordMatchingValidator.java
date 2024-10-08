package com.GHTK.Social_Network.common.customAnnotation.logic;


import com.GHTK.Social_Network.common.customAnnotation.config.PasswordMatching;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Objects;

public class PasswordMatchingValidator implements ConstraintValidator<PasswordMatching, Object> {
  private String password;

  private String confirmPassword;

  @Override
  public void initialize(PasswordMatching matching) {
    this.password = matching.password();
    this.confirmPassword = matching.confirmPassword();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
    var passwordValue = new BeanWrapperImpl(value).getPropertyValue(password);
    var confirmPasswordValue = new BeanWrapperImpl(value).getPropertyValue(confirmPassword);
    return Objects.equals(passwordValue, confirmPasswordValue);
  }
}
