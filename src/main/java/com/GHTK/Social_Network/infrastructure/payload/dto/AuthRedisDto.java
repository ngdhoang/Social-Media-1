package com.GHTK.Social_Network.infrastructure.payload.dto;

import com.GHTK.Social_Network.infrastructure.payload.requests.RegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class AuthRedisDto {
  private RegisterRequest registerRequest;

  private String otp;

  private Date createTime;

  private int count;
}
