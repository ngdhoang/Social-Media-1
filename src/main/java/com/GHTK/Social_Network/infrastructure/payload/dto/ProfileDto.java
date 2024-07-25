package com.GHTK.Social_Network.infrastructure.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
  private Long profileId;

  private String lastName;

  private String firstName;

  private String userEmail;

  private String avatar = "";

  private LocalDate dob;

  private String phoneNumber;

  private String homeTown;

  private String schoolName;

  private String workPlace;

  private Boolean isProfilePublic;

  public ProfileDto(Long userId, String firstName, String lastName, String avatar) {
    this.profileId = userId;
    this.lastName = lastName;
    this.firstName = firstName;
    this.avatar = avatar;
  }
}
