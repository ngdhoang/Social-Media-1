package com.GHTK.Social_Network.application.customAnnotation.config;

import com.GHTK.Social_Network.application.customAnnotation.logic.SortByValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = SortByValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidSortBy {

    String message() default "Sort by: Must be a valid sort by or not.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
