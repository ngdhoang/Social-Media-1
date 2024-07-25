package com.GHTK.Social_Network.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
  private Long userId;

  private LocalDate dob;

  private String phoneNumber;

  private String homeTown;

  private String schoolName;

  private String workPlace;

  private Boolean isDobPublic;

  private Boolean isPhoneNumberPublic;

  private Boolean isHomeTownPublic;

  private Boolean isSchoolNamePublic;

  private Boolean isWorkPlacePublic;
}
