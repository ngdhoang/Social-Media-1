package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateProfileRequest;

public interface ProfilePort {
  User takeProfileById(Long id);

  void updateProfile(UpdateProfileRequest updateProfileRequest, Long userId);

  Boolean setStateProfileById(Integer i, Long userId);
}
