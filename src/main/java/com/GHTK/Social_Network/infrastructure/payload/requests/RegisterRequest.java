package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.common.customAnnotation.config.PasswordMatching;
import com.GHTK.Social_Network.common.customAnnotation.config.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatching(
        password = "password",
        confirmPassword = "confirmPassword",
        message = "Passwords do not match!"
)
public class RegisterRequest {
  @NotBlank(message = "firstName cannot blank")
  private String firstName;

  @NotBlank(message = "lastName cannot blank")
  private String lastName;

  @NotBlank(message = "email cannot blank")
  @Size(max = 50)
  @Email(message = "Email invalidate")
  private String userEmail;

  @NotBlank(message = "password cannot blank")
  @StrongPassword
  private String password;

  @NotBlank(message = "confirm password cannot blank")
  private String confirmPassword;

  private String code;

  private String otp;
}
