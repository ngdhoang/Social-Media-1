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
public class UserDto {
  private Long userId;

  private String lastName;

  private String firstName;

  private String userEmail;

  private String avatar = "";

  private String background = "";

  private Boolean isProfilePublic;

  private FieldVisibilityDto<LocalDate> dob;

  private FieldVisibilityDto<String> phoneNumber;

  private FieldVisibilityDto<String> homeTown;

  private FieldVisibilityDto<String> schoolName;

  private FieldVisibilityDto<String> workPlace;

  private FieldVisibilityDto<String> gender;
}
