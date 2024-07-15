package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.ProfileStateRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateProfileRequest;

public interface ProfilePortInput {
  ProfileDto getProfile(Long id);

  ProfileDto updateProfile(UpdateProfileRequest updateProfileRequest);

  ProfileDto setStateProfile(ProfileStateRequest profileStateDto);

  ProfileDto updateAvatarProfile(ImageDto imageDto);
}
