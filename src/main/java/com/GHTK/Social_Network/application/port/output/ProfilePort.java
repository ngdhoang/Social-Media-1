package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateProfileRequest;

import java.util.Optional;

public interface ProfilePort {
  Optional<UserEntity> takeProfileById(Long id);

  Boolean updateProfile(UpdateProfileRequest updateProfileRequest, Long userId);

  Boolean setStateProfileById(Integer i, Long userId);
}
