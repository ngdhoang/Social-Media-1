package com.GHTK.Social_Network.infrastructure.payload.requests.auth;

import lombok.Data;

import java.util.Date;

@Data
public class OTPRequest {
  private String otp;

  private Date createTime;

  private int count;
}
