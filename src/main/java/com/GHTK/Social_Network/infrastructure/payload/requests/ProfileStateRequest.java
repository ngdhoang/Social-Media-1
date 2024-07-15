package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.application.customAnnotation.config.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ProfileStateRequest {
    private int isProfilePublic;
}