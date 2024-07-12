package com.ghtk.social_network.application.config;


import com.ghtk.social_network.application.config.logic.PasswordMatchingValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordMatchingValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatching {
  String password();

  String confirmPassword();

  String message() default "Passwords must match!";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}