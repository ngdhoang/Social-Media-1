package com.GHTK.Social_Network.application.customAnnotation.config;

import com.GHTK.Social_Network.application.customAnnotation.logic.EFriendShipStatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = EFriendShipStatusValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidEFriendShipStatus {

    String message() default "EFriendShipStatus: Must be a valid friendship or not.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
