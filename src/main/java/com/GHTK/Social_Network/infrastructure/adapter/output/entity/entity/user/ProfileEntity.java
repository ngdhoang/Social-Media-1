package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name = "profile")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileEntity {
  @Id
  private Long userId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "user_id")
  private UserEntity user;

  private LocalDate dob;

  private String phoneNumber;

  private String homeTown;

  private String schoolName;

  private String workPlace;

  private EGenderEntity gender;

  private Boolean isDobPublic = true;

  private Boolean isPhoneNumberPublic = true;

  private Boolean isHomeTownPublic = true;

  private Boolean isSchoolNamePublic = true;

  private Boolean isWorkPlacePublic = true;

  private Boolean isGenderPublic = true;
}
