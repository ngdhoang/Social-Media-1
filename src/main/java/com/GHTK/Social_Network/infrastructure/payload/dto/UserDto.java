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

  private FieldVisibilityDto<LocalDate> dob;

  private FieldVisibilityDto<LocalDate> phoneNumber;

  private FieldVisibilityDto<LocalDate> homeTown;

  private FieldVisibilityDto<LocalDate> schoolName;

  private FieldVisibilityDto<LocalDate> workPlace;

  private FieldVisibilityDto<LocalDate> gender;

  private FieldVisibilityDto<LocalDate> isProfilePublic;
}
