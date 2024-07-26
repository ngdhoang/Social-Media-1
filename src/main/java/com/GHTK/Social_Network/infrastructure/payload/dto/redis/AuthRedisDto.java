package com.GHTK.Social_Network.infrastructure.payload.dto.redis;

import com.GHTK.Social_Network.infrastructure.payload.requests.RegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRedisDto {
  private RegisterRequest registerRequest;

  private String otp;

  private Date createTime;

  private int count;
}
