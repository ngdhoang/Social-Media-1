package com.GHTK.Social_Network.common.customAnnotation.logic;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidEReactionPostType;
import com.GHTK.Social_Network.domain.model.EReactionType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EReactionPostTypeValidator implements ConstraintValidator<ValidEReactionPostType, String> {
  @Override
  public void initialize(ValidEReactionPostType constraintAnnotation) {
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    if (value == null) {
      return true;
    }

    try {
      EReactionType.valueOf(value.toUpperCase());
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
