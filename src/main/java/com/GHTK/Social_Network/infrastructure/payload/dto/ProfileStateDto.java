package com.GHTK.Social_Network.infrastructure.payload.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@NotBlank(message = "State cannot blank")
public class ProfileStateDto {
    private boolean isDobPublic;

    private boolean isPhoneNumberPublic;

    private boolean isHomeTownPublic;

    private boolean isSchoolNamePublic;

    private boolean isWorkPlacePublic;

    private boolean isGenderPublic;
}