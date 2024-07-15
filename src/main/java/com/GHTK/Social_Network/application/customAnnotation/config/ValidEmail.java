package com.GHTK.Social_Network.application.customAnnotation.config;

import com.GHTK.Social_Network.application.customAnnotation.logic.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = EmailValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidEmail {

    String message() default "Email: Must be a valid email address.";


    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
