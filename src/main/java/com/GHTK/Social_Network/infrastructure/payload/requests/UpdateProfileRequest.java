package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String firstName;

    private String lastName;

    @Email(message = "Email invalidate")
    private String email;

    @ValidPhoneNumber
    private String phoneNumber;

    private LocalDate dob;

    private String homeTown;

    private String schoolName;

    private String workPlace;

    private Boolean isProfilePublic;

    private String code;
}