package com.GHTK.Social_Network.infrastructure.payload.requests.profile;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidDateOfBirth;
import com.GHTK.Social_Network.common.customAnnotation.config.ValidPattern;
import com.GHTK.Social_Network.common.customAnnotation.logic.CustomPatternValidator;
import com.GHTK.Social_Network.common.customAnnotation.logic.DateOfBirthValidator;
import com.GHTK.Social_Network.domain.model.user.EGender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "firstName cannot blank")
    private String firstName;

    @NotBlank(message = "lastName cannot blank")
    private String lastName;

    @ValidPattern(CustomPatternValidator.PHONE_NUMBER)
    private String phoneNumber;

    @ValidDateOfBirth
    private LocalDate dob;

    private Integer homeTown;

    private String schoolName;

    private String workPlace;

    private Boolean isProfilePublic;

    private String code;

    @ValidPattern(CustomPatternValidator.GENDER)
    private String gender;

}