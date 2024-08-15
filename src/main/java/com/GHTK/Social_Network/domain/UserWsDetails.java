package com.GHTK.Social_Network.domain;

import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWsDetails {
  private UserBasicDto user;

  private String fingerprinting;

  private String session;
}
