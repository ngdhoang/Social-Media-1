package com.GHTK.Social_Network.common.customAnnotation.config;

import com.GHTK.Social_Network.common.customAnnotation.logic.EReactionPostTypeValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Constraint(validatedBy = EReactionPostTypeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidEReactionPostType {

  String message() default "EReactionPostType: Must be a valid reaction type or not.";

  Class<?>[] groups() default {};

  Class<?>[] payload() default {};
}
