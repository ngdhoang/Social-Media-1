package com.GHTK.Social_Network.infrastructure.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
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
    private String message;
}
