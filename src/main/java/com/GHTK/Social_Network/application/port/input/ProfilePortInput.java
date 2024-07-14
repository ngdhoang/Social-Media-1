package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;

public interface ProfilePortInput {
  ProfileDto getProfile(Long id);

  Boolean updateProfile(ProfileDto profileDto);

  Boolean setStateProfile(Integer state);
}
