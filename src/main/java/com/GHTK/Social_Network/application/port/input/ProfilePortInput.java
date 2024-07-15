package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;

public interface ProfilePortInput {
  ProfileDto getProfile(Long id);

  ProfileDto updateProfile(ProfileDto profileDto);

  ProfileDto setStateProfile(Integer state);

  ProfileDto updateAvatarProfile(ImageDto imageDto);
}
