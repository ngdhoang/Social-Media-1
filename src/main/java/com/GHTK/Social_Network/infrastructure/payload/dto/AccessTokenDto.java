package com.GHTK.Social_Network.infrastructure.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccessTokenDto {
  private String fingerprinting;

  private String tokenType = "BEARER";

  private boolean revoked;

  private boolean expired;

  private Long userId;
}
