package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import com.GHTK.Social_Network.application.port.input.ProfilePortInput;
import com.GHTK.Social_Network.application.port.output.CloudPort;
import com.GHTK.Social_Network.application.port.output.ImageHandlerPort;
import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.application.port.output.RedisProfilePort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ProfileMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.ProfileStateRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService implements ProfilePortInput {
  private final AuthPort authPort;
  private final ProfilePort profilePort;
  private final ImageHandlerPort imageHandlerPort;
  private final CloudPort cloudPort;

  private final RedisProfilePort redisProfilePort;
  private final ProfileMapper profileMapper;

  private User getUserAuth() {
    User user = authPort.getUserAuth();
    return user == null ? User.builder().userId(0L).build() : user;
  }

  @Override
  public ProfileDto getProfile(Long id) {
    if (id < 0) {
      id = getUserAuth().getUserId();
    }
    if (Boolean.TRUE.equals(redisProfilePort.findByKey(String.valueOf(id)))) {
      if (redisProfilePort.findByKey(String.valueOf(id)) == null && !getUserAuth().getUserId().equals(id)) {
        return null;
      }
      return redisProfilePort.findByKey(String.valueOf(id));
    }
    Optional<User> user = profilePort.takeProfileById(id);

    if (user.isEmpty()) {
      return null;
    }

    Boolean isProfilePublic = user.get().getIsProfilePublic();

    ProfileDto profileDto = profileMapper.userToProfileDto(user.get());
    if (isProfilePublic || user.get().getUserId().equals(getUserAuth().getUserId())) {
      redisProfilePort.createOrUpdate(String.valueOf(id), profileDto);
      return profileDto;
    }
    return null;
  }

  @Override
  public ProfileDto updateProfile(UpdateProfileRequest updateProfileRequest) {
    Long userId = getUserAuth().getUserId();

    Boolean isUpdateProfile = profilePort.updateProfile(updateProfileRequest, userId);
    Optional<User> profileDto = profilePort.takeProfileById(userId);
    if (isUpdateProfile) {
      redisProfilePort.createOrUpdate(String.valueOf(userId), profileMapper.userToProfileDto(profileDto.get()));
    }
    return redisProfilePort.findByKey(String.valueOf(userId));
  }

  @Override
  public ProfileDto setStateProfile(ProfileStateRequest profileStateRequest) {
    Integer state = profileStateRequest.getIsProfilePublic();
    Boolean isSetStateProfile = profilePort.setStateProfileById(state, getUserAuth().getUserId());
    if (isSetStateProfile) {
      ProfileDto profileDto = redisProfilePort.findByKey(String.valueOf(getUserAuth().getUserId()));
      if (profileDto != null) {
        profileDto.setIsProfilePublic(state == 1);
      }
      redisProfilePort.createOrUpdate(String.valueOf(profileDto.getProfileId()), profileDto);
    }
    return redisProfilePort.findByKey(String.valueOf(getUserAuth().getUserId()));
  }

  @Override
  public ProfileDto updateAvatarProfile(MultipartFile file) {
    String url = (String) cloudPort.uploadPictureByFile(file, ImageHandlerPortInput.MAX_SIZE_AVATAR).get("url");
    Boolean check = imageHandlerPort.saveAvatar(url, getUserAuth().getUserId());
    if (check) {
      ProfileDto profileDto = redisProfilePort.findByKey(String.valueOf(getUserAuth().getUserId()));
      profileDto.setAvatar(url);
      redisProfilePort.createOrUpdate(String.valueOf(profileDto.getProfileId()), profileDto);
      return profileDto;
    } else {
      throw new CustomException("Error updating avatar", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
