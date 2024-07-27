package com.GHTK.Social_Network.infrastructure.payload.requests.profile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfilePrivacyRequest {
  @NotNull(message = "State cannot null")
  private Boolean isProfilePrivacy;
}
