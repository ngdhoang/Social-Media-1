package com.GHTK.Social_Network.infrastructure.payload.requests.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfilePrivacyRequest {
  @NotBlank(message = "State cannot blank")
  private boolean isProfilePrivacy;
}
