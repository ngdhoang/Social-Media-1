package com.GHTK.Social_Network.infrastructure.payload.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {
  @Size(max = 50)
  @Email(message = "Email invalidate")
  @NotBlank(message = "email cannot blank")
  private String userEmail;

  @NotBlank(message = "password cannot blank")
  private String password;
}