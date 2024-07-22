package com.GHTK.Social_Network.common.customAnnotation.logic;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidEFriendShipStatus;
import com.GHTK.Social_Network.infrastructure.entity.EFriendshipStatusEntity;
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
            EFriendshipStatusEntity.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
