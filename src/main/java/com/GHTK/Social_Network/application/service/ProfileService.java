package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import com.GHTK.Social_Network.application.port.input.ProfilePortInput;
import com.GHTK.Social_Network.application.port.output.CloudPort;
import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.application.port.output.RedisProfilePort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.user.Profile;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.UserMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.ProfileStateDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.profile.UpdateProfilePrivacyRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.profile.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileService implements ProfilePortInput {
  private final AuthPort authPort;
  private final ProfilePort profilePort;
  private final CloudPort cloudPort;

  private final RedisProfilePort redisProfilePort;
  private final UserMapper userMapper;

  @Override
  public UserDto getProfile(Long id) {
    User currentUser = authPort.getUserAuthOrDefaultVirtual();
    id = id < 0 ? currentUser.getUserId() : id; // if id < 0 take profile auth

    UserDto userDto = new UserDto();
    UserDto cachedProfile = redisProfilePort.findByKey(String.valueOf(id));
    if (cachedProfile != null) {
      if (!cachedProfile.getIsProfilePublic() && !currentUser.getUserId().equals(id)) {
        throw new CustomException("You don't have permission to view this profile", HttpStatus.FORBIDDEN);
      }
      userDto = cachedProfile;
    } else {
      User user = profilePort.takeUserById(id)
              .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));

      Profile profile = profilePort.takeProfileById(id);

      if (!user.getIsProfilePublic() && !user.getUserId().equals(currentUser.getUserId())) {
        throw new CustomException("You don't have permission to view this profile", HttpStatus.FORBIDDEN);
      }

      userDto = userMapper.userAndProfileToUserDto(user, profile);

      redisProfilePort.createOrUpdate(String.valueOf(id), userDto);
    }

    return toResponse(currentUser.getUserId(), id, userDto);
  }

  @Override
  public UserDto updateProfile(UpdateProfileRequest updateProfileRequest) {
    User currentUser = authPort.getUserAuth();
    Long userId = currentUser.getUserId();

    Boolean isUpdateProfile = profilePort.updateProfile(updateProfileRequest, userId);
    Profile profile = profilePort.takeProfileById(userId);
    UserDto userDto = userMapper.userAndProfileToUserDto(currentUser, profile);
    if (isUpdateProfile) {
      redisProfilePort.createOrUpdate(String.valueOf(userId), userDto);
    }
    return toResponse(userId, userId, userDto);
  }

  @Override
  public UserDto setStateProfile(ProfileStateDto profileStateDto) {
    User currentUser = authPort.getUserAuth();
    Long userId = currentUser.getUserId();

    if (profilePort.setProfileStateById(profileStateDto, currentUser.getUserId())) {
      Profile profile = profilePort.takeProfileById(currentUser.getUserId());
      UserDto userDto = userMapper.userAndProfileToUserDto(currentUser, profile);
      redisProfilePort.createOrUpdate(String.valueOf(userDto.getUserId()), userDto);
      return toResponse(userId, userId, userDto);
    }
    throw new CustomException("Error updating avatar", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  public UserDto setProfilePrivacy(UpdateProfilePrivacyRequest updateProfilePrivacyRequest) {
    User currentUser = authPort.getUserAuth();
    Long userId = currentUser.getUserId();

    Boolean state = updateProfilePrivacyRequest.getIsProfilePrivacy();
    User user = profilePort.setProfilePrivacyById(state, currentUser.getUserId());
    if (user == null) {
      throw new CustomException("Error updating state", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    UserDto userDto = redisProfilePort.findByKey(String.valueOf(currentUser.getUserId()));
    if (userDto != null) {
      userDto.setIsProfilePublic(state);
      return userDto;
    }
    Profile profile = profilePort.takeProfileById(userId);
    userDto = userMapper.userAndProfileToUserDto(user, profile);
    redisProfilePort.createOrUpdate(String.valueOf(userId), userDto);
    return toResponse(userId, userId, userDto);
  }

  @Override
  public UserDto updateAvatarProfile(MultipartFile file) {
    User currentUser = authPort.getUserAuth();
    Long userId = currentUser.getUserId();

    String url = (String) cloudPort.uploadPictureByFile(file, ImageHandlerPortInput.MAX_SIZE_AVATAR).get("url");
    String avatarOld = profilePort.saveAvatar(url, currentUser.getUserId());
    if (avatarOld != null) {
      Profile profile = profilePort.takeProfileById(userId);
      UserDto userDto = userMapper.userAndProfileToUserDto(currentUser, profile);
      userDto.setAvatar(url);
      redisProfilePort.createOrUpdate(String.valueOf(userDto.getUserId()), userDto);
      cloudPort.deletePictureByUrl(avatarOld);
      return toResponse(userId, userId, userDto);
    }
    throw new CustomException("Error updating avatar", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  public UserDto updateBackgroundProfile(MultipartFile background) {
    User currentUser = authPort.getUserAuth();
    Long userId = currentUser.getUserId();

    String url = (String) cloudPort.uploadPictureByFile(background, ImageHandlerPortInput.MAX_SIZE_AVATAR).get("url");
    String backgroundOld = profilePort.saveBackground(url, currentUser.getUserId());
    if (backgroundOld != null) {
      Profile profile = profilePort.takeProfileById(userId);
      UserDto userDto = userMapper.userAndProfileToUserDto(currentUser, profile);
      userDto.setBackground(url);
      redisProfilePort.createOrUpdate(String.valueOf(userDto.getUserId()), userDto);
      cloudPort.deletePictureByUrl(backgroundOld);
      return toResponse(userId, userId, userDto);
    } else {
      throw new CustomException("Error updating avatar", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private UserDto toResponse(Long currentUserId, Long profileId, UserDto userDto) {
    return profileId.equals(currentUserId) ? userMapper.userDtoToUserPublicDto(userDto) : userMapper.userDtoToUserPrivateDto(userDto);
  }
}