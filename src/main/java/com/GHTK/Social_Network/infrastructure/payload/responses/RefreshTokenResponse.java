package com.GHTK.Social_Network.infrastructure.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RefreshTokenResponse {
  private String accessToken;
}
