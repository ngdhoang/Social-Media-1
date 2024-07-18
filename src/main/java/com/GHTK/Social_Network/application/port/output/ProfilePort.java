package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.entity.user.User;

public interface ProfilePort {
  User takeProfileById(Long id);

  Boolean updateProfile(User user);

  Boolean setStateProfileById(Integer i, Long userId);
}
