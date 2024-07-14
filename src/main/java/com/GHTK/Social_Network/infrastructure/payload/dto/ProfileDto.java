package com.GHTK.Social_Network.infrastructure.payload.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileDto {
  private Long profileId;

  private String lastName;

  private String firstName;

  private String userEmail;

  private LocalDate dob;

  private String phoneNumber;

  private String homeTown;

  private String schoolName;

  private String workPlace;
}
