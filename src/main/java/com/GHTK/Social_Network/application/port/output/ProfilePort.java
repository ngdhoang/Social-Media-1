package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateProfileRequest;

import java.util.Optional;

public interface ProfilePort {
  Optional<User> takeProfileById(Long id);

  Boolean updateProfile(UpdateProfileRequest updateProfileRequest, Long userId);

  Boolean setStateProfileById(Integer i, Long userId);
}
