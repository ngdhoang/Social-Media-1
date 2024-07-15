package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.application.customAnnotation.config.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;

    private String lastName;

    @Email(message = "Email invalidate")
    private String email;

    @ValidPhoneNumber
    private String phoneNumber;

    private String homeTown;

    private String schoolName;

    private String workPlace;

    private String code;
}
