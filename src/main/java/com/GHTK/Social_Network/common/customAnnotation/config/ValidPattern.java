package com.GHTK.Social_Network.common.customAnnotation.config;

import com.GHTK.Social_Network.common.customAnnotation.logic.CustomPatternValidator;
import com.GHTK.Social_Network.common.customAnnotation.logic.PatternValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PatternValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPattern {
  String message() default "Invalid format";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  CustomPatternValidator value();
}
