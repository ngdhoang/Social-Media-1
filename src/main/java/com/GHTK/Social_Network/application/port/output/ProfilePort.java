package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.model.Profile;
import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.payload.requests.profile.UpdateProfileRequest;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileStateDto;

import java.util.Optional;

public interface ProfilePort {
  Optional<User> takeUserById(Long id);

  Profile takeProfileById(Long id);

  Boolean updateProfile(UpdateProfileRequest updateProfileRequest, Long userId);

  Boolean setProfilePrivacyById(Boolean state, Long userId);

  Boolean setProfileStateById(ProfileStateDto profileStateDto, Long userId);

  String saveAvatar(String avatar, Long id);

  String saveBackground(String avatar, Long id);
}
