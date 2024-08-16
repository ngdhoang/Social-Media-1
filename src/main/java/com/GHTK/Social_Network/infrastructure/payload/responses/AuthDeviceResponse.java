package com.GHTK.Social_Network.infrastructure.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDeviceResponse {
  private String otp;

  private String key;
}
