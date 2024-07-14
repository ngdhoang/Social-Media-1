package com.GHTK.Social_Network.infrastructure.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequest {
    // idProfile -> idUser chinh chu profile
    private Long idProfile;
    // idUser -> cua User dang view profile
    //fields profile
    private Long idUser;
    private String lastName;
    private String firstName;
    private String userEmail;
    //setup
    //private String avatar;
    private LocalDate dob;
    private String phoneNumber;
    private String homeTown;
    private String schoolName;
    private String workPlace;
}
