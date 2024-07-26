package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.dto.UserDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.profile.UpdateProfilePrivacyRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.profile.UpdateProfileRequest;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileStateDto;
import org.springframework.web.multipart.MultipartFile;

public interface ProfilePortInput {
  UserDto getProfile(Long id);

  UserDto updateProfile(UpdateProfileRequest updateProfileRequest);

  UserDto setStateProfile(ProfileStateDto profileStateDto);

  UserDto setProfilePrivacy(UpdateProfilePrivacyRequest profileStateDto);

  UserDto updateAvatarProfile(MultipartFile file);

  UserDto updateBackgroundProfile(MultipartFile background);
}
