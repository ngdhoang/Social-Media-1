package com.ghtk.social_network.domain.model.form;

import com.ghtk.social_network.domain.model.user.EStatusUser;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateForm {
    private String firstName;

    private String lastName;

    private String userEmail;

    private String password;

    private String oldPassword;

    private String avatar;

    private LocalDate dob;

    private String phoneNumber;

    private String homeTown;

    private String schoolName;

    private String workPlace;

    @Enumerated(EnumType.STRING)
    private EStatusUser statusUser;
}
