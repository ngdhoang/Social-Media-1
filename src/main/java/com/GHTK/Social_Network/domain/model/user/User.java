package com.GHTK.Social_Network.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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