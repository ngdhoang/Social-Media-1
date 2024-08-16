package com.GHTK.Social_Network.common.customAnnotation.config;


import com.GHTK.Social_Network.common.customAnnotation.logic.DateOfBirthValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = DateOfBirthValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidDateOfBirth {

    String message() default "Date of birth: Must be at least 16 years old.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
