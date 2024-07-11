package com.GHTK.Social_Network.authentication.application.payloads.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
  @JsonProperty("accessToken")
  private String accessToken;

  @JsonProperty("refreshToken")
  private String refreshToken;

  @JsonProperty("role")
  private String role;
}
