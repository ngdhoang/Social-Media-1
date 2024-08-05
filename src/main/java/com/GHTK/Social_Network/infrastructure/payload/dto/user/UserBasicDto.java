package com.GHTK.Social_Network.infrastructure.payload.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBasicDto {
  private Long userId;

  private String firstName;

  private String lastName;

  private String userEmail;

  private String avatar;
}