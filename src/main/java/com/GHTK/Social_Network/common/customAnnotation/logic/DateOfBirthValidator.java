package com.GHTK.Social_Network.common.customAnnotation.logic;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidDateOfBirth;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateOfBirthValidator  implements ConstraintValidator<ValidDateOfBirth, LocalDate>{

        @Override
        public void initialize(ValidDateOfBirth constraintAnnotation) {
        }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            return true;
        }
        LocalDate now = LocalDate.now();
        return now.minusYears(16).isAfter(localDate);
    }

}
