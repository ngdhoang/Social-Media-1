package com.GHTK.Social_Network.infrastructure.payload.dto.user;

import lombok.Data;

@Data
public class ProfileStateDto {
    private Boolean isDobPublic;

    private Boolean isPhoneNumberPublic;

    private Boolean isHomeTownPublic;

    private Boolean isSchoolNamePublic;

    private Boolean isWorkPlacePublic;

    private Boolean isGenderPublic;
}