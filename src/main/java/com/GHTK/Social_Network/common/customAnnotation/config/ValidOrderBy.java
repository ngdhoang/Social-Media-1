package com.GHTK.Social_Network.common.customAnnotation.config;

import com.GHTK.Social_Network.common.customAnnotation.logic.OrderByValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = OrderByValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidOrderBy {

    String message() default "Order by: Must be a valid order by or not.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
