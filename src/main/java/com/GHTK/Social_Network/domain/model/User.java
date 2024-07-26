package com.GHTK.Social_Network.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
  private Long userId;

  private String firstName;

  private String lastName;

  private String userEmail;

  private String password;

  private String oldPassword;

  private String avatar;

  private String background;

  private Boolean isProfilePublic = true;

  private EStatusUser statusUser;

  private ERole role;

  public User(String lastName, String firstName, String userEmail, String password) {
    this.lastName = lastName;
    this.firstName = firstName;
    this.userEmail = userEmail;
    this.password = password;
  }
}