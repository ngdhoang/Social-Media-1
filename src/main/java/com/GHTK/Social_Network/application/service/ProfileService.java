package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import com.GHTK.Social_Network.application.port.input.ProfilePortInput;
import com.GHTK.Social_Network.application.port.output.CloudPort;
import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.application.port.output.RedisProfilePort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.Profile;
import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.UserMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileStateDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.UserDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.ProfileRedisDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.profile.UpdateProfilePrivacyRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.profile.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileService {
//  private final AuthPort authPort;
//  private final ProfilePort profilePort;
//  private final CloudPort cloudPort;
//
//  private final RedisProfilePort redisProfilePort;
//  private final UserMapper userMapper;
//
//  @Override
//  public UserDto getProfile(Long id) {
//    User currentUser = authPort.getUserAuthOrDefaultVirtual();
//    id = id < 0 ? currentUser.getUserId() : id; // if id < 0 take profile auth
//
//    ProfileRedisDto cachedProfile = redisProfilePort.findByKey(String.valueOf(id));
//    if (cachedProfile != null) {
//      User user = cachedProfile.getUser();
//      Profile profile = cachedProfile.getProfile();
//      if (!user.getIsProfilePublic() && !currentUser.getUserId().equals(id)) {
//        throw new CustomException("You don't have permission to view this profile", HttpStatus.FORBIDDEN);
//      }
//      return userMapper.userAndProfileToUserDtoPrivate(user, profile);
//    }
//
//    User user = profilePort.takeUserById(id)
//            .orElseThrow(() -> new CustomException("Profile not found", HttpStatus.NOT_FOUND));
//
//    Profile profile = profilePort.takeProfileById(id);
//
//    if (!user.getIsProfilePublic() && !user.getUserId().equals(currentUser.getUserId())) {
//      throw new CustomException("You don't have permission to view this profile", HttpStatus.FORBIDDEN);
//    }
//
//    UserDto userDto = userMapper.userAndProfileToUserDtoPrivate(user, profile);
//
//    redisProfilePort.createOrUpdate(String.valueOf(id), new ProfileRedisDto(user, profile));
//    return userDto;
//  }
//
//  @Override
//  public UserDto updateProfile(UpdateProfileRequest updateProfileRequest) {
//    User currentUser = authPort.getUserAuth();
//    Long userId = currentUser.getUserId();
//
//    Boolean isUpdateProfile = profilePort.updateProfile(updateProfileRequest, userId);
//    User user = profilePort.takeUserById(userId).get();
//    Profile profile = profilePort.takeProfileById(userId);
//    if (isUpdateProfile) {
//      redisProfilePort.createOrUpdate(String.valueOf(userId), new ProfileRedisDto(user, profile));
//    }
//    return userMapper.userAndProfileToUserDtoPrivate(user, profile);
//  }
//
//  @Override
//  public UserDto setStateProfile(ProfileStateDto profileStateDto) {
//    User currentUser = authPort.getUserAuth();
//
//    if (profilePort.setProfileStateById(profileStateDto, currentUser.getUserId())) {
//      UserDto userDto = redisProfilePort.findByKey(String.valueOf(currentUser.getUserId()));
//      if (userDto != null) {
//        userDto
//      }
//      redisProfilePort.createOrUpdate(String.valueOf(userDto.getUserId()), userDto);
//    }
//    return redisProfilePort.findByKey(String.valueOf(currentUser.getUserId()));
//  }
//
//  @Override
//  public UserDto setProfilePrivacy(UpdateProfilePrivacyRequest updateProfilePrivacyRequest) {
//    User currentUser = authPort.getUserAuth();
//
//    Boolean state = updateProfilePrivacyRequest.isProfilePrivacy();
//    if (profilePort.setProfilePrivacyById(state, currentUser.getUserId())) {
//      UserDto userDto = redisProfilePort.findByKey(String.valueOf(currentUser.getUserId()));
//      if (userDto != null) {
//        userDto.setIsProfilePublic(state);
//      }
//      redisProfilePort.createOrUpdate(String.valueOf(userDto.getUserId()), userDto);
//    }
//    return redisProfilePort.findByKey(String.valueOf(currentUser.getUserId()));
//  }
//
//  @Override
//  public UserDto updateAvatarProfile(MultipartFile file) {
//    User currentUser = authPort.getUserAuth();
//
//    String url = (String) cloudPort.uploadPictureByFile(file, ImageHandlerPortInput.MAX_SIZE_AVATAR).get("url");
//    String avatarOld = profilePort.saveAvatar(url, currentUser.getUserId());
//    if (avatarOld != null) {
//      UserDto userDto = redisProfilePort.findByKey(String.valueOf(currentUser.getUserId()));
//      userDto.setAvatar(url);
//      redisProfilePort.createOrUpdate(String.valueOf(userDto.getUserId()), userDto);
//      cloudPort.deletePictureByUrl(avatarOld);
//      return userDto;
//    } else {
//      throw new CustomException("Error updating avatar", HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//  }
//
//  @Override
//  public UserDto updateBackgroundProfile(MultipartFile background) {
//    User currentUser = authPort.getUserAuth();
//
//    String url = (String) cloudPort.uploadPictureByFile(background, ImageHandlerPortInput.MAX_SIZE_AVATAR).get("url");
//    String backgroundOld = profilePort.saveBackground(url, currentUser.getUserId());
//    if (backgroundOld != null) {
//      UserDto userDto = redisProfilePort.findByKey(String.valueOf(currentUser.getUserId()));
//      userDto.setBackground(url);
//      redisProfilePort.createOrUpdate(String.valueOf(userDto.getUserId()), userDto);
//      cloudPort.deletePictureByUrl(backgroundOld);
//      return userDto;
//    } else {
//      throw new CustomException("Error updating avatar", HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//  }
}