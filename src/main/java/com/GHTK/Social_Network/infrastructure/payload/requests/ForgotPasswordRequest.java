package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.application.customAnnotation.config.PasswordMatching;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@PasswordMatching(
        password = "newPassword",
        confirmPassword = "confirmPassword",
        message = "Passwords do not match!"
)
public class ForgotPasswordRequest {
  @NotBlank(message = "email cannot blank")
  @Size(max = 50)
  @Email(message = "Email invalidate")
  private String userEmail;

  private String newPassword;

  private String confirmPassword;

  private String otp;

  private Date createTime;

  private int count;
}
