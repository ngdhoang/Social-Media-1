package com.GHTK.Social_Network.common.customAnnotation.logic;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidEFriendShipStatus;
import com.GHTK.Social_Network.domain.model.EFriendshipStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EFriendShipStatusValidator implements ConstraintValidator<ValidEFriendShipStatus, String> {
  @Override
  public void initialize(ValidEFriendShipStatus constraintAnnotation) {
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    if (value == null) {
      return true;
    }

    try {
      EFriendshipStatus.valueOf(value.toUpperCase());
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
