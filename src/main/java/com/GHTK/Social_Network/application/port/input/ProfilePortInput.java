package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.dto.UserDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.ProfileStateRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateProfileRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ProfilePortInput {
  UserDto getProfile(Long id);

  UserDto updateProfile(UpdateProfileRequest updateProfileRequest);

  UserDto setStateProfile(ProfileStateRequest profileStateDto);

  UserDto updateAvatarProfile(MultipartFile file);
}
