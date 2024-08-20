package com.GHTK.Social_Network.domain.model.user;

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

  private Integer homeTown;

  private String schoolName;

  private String workPlace;

  private EGender gender;

  private Boolean isDobPublic;

  private Boolean isPhoneNumberPublic;

  private Boolean isHomeTownPublic;

  private Boolean isSchoolNamePublic;

  private Boolean isWorkPlacePublic;

  private Boolean isGenderPublic;

}
