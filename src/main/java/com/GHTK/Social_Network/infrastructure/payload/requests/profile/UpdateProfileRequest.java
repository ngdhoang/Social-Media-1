package com.GHTK.Social_Network.infrastructure.payload.requests.profile;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "firstName cannot blank")
    private String firstName;

    @NotBlank(message = "lastName cannot blank")
    private String lastName;

    @ValidPhoneNumber
    private String phoneNumber;

    private LocalDate dob;

    private String homeTown;

    private String schoolName;

    private String workPlace;

    private Boolean isProfilePublic;

    private String code;
}