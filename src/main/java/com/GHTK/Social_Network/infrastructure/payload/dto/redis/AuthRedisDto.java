package com.GHTK.Social_Network.infrastructure.payload.dto.redis;

import com.GHTK.Social_Network.infrastructure.payload.requests.RegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRedisDto {
  private RegisterRequest registerRequest;

  private String fingerprinting;

  private String userAgent;

  private List<String> otp;

  private Date createAt;

  private int count;

  public AuthRedisDto(List<String> otp, Date createAt, int count) {
    this.otp = otp;
    this.createAt = createAt;
    this.count = count;
  }
}
