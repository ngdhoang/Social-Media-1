package com.GHTK.Social_Network.infrastructure.payload.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationRequest {
  @NotBlank(message = "email cannot blank")
  @Size(max = 50)
  @Email(message = "Email invalidate")
  private String userEmail;

  @NotBlank(message = "password cannot blank")
  private String password;
}