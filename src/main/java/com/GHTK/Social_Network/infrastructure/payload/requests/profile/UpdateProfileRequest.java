package com.GHTK.Social_Network.infrastructure.payload.requests.profile;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidPattern;
import com.GHTK.Social_Network.common.customAnnotation.logic.CustomPatternValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "firstName cannot blank")
    private String firstName;

    @NotBlank(message = "lastName cannot blank")
    private String lastName;

    @ValidPattern(CustomPatternValidator.PHONE_NUMBER)
    private String phoneNumber;

    private LocalDate dob;

    private Integer homeTown;

    private String schoolName;

    private String workPlace;

    private Boolean isProfilePublic;

    private String code;
}