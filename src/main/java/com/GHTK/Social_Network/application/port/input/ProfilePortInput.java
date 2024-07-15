package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.ProfileStateRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateProfileRequest;

public interface ProfilePortInput {
  ProfileDto getProfile(Long id);

  ProfileDto updateProfile(UpdateProfileRequest profileDto);

  ProfileDto setStateProfile(ProfileStateRequest profileStateDto);
}
