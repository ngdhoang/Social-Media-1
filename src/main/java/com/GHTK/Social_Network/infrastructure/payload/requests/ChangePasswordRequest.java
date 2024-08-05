package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.common.customAnnotation.config.PasswordMatching;
import com.GHTK.Social_Network.common.customAnnotation.config.ValidPattern;
import com.GHTK.Social_Network.common.customAnnotation.logic.CustomPatternValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@PasswordMatching(
        password = "newPassword",
        confirmPassword = "confirmNewPassword",
        message = "Passwords do not match!"
)
public class ChangePasswordRequest {
    @NotBlank(message = "Old password cannot blank")
    private String oldPassword;

    @NotBlank(message = "New password cannot blank")
    @ValidPattern(CustomPatternValidator.STRONG_PASSWORD)
    private String newPassword;

    @NotBlank(message = "Confirm new password cannot blank")
    private String confirmNewPassword;


}
