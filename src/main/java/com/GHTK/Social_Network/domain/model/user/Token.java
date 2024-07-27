package com.GHTK.Social_Network.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {
  private Long tokenId;

  private String token;

  private String tokenType = "BEARER";

  private boolean revoked;

  private boolean expired;

  private Long userId;
}