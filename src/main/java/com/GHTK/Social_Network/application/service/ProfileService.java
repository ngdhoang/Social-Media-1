package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.CloudServicePortInput;
import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import com.GHTK.Social_Network.application.port.input.ProfilePortInput;
import com.GHTK.Social_Network.application.port.output.AuthPort;
import com.GHTK.Social_Network.application.port.output.ImageHandlerPort;
import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.exception.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ProfileMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.ProfileStateRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService implements ProfilePortInput {
  private final ProfilePort profilePort;

  private final RedisTemplate<String, ProfileDto> profileDtoRedisTemplate;

  private final AuthPort authenticationRepositoryPort;

  private final ImageHandlerPort imageHandlerPort;

  private final CloudServicePortInput cloudServicePortInput;

  private User getUserAuth() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated() ||
            authentication instanceof AnonymousAuthenticationToken) {
      return User.builder().userId(0L).build();
    }

    Object principal = authentication.getPrincipal();
    String username;

    if (principal instanceof UserDetails) {
      username = ((UserDetails) principal).getUsername();
    } else if (principal instanceof String) {
      username = (String) principal;
    } else {
      throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }

    return authenticationRepositoryPort.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid token"));
  }

  @Override
  public ProfileDto getProfile(Long id) {
    if (id < 0) {
      id = getUserAuth().getUserId();
    }
    if (Boolean.TRUE.equals(profileDtoRedisTemplate.hasKey(String.valueOf(id)))) {
      if (!profileDtoRedisTemplate.opsForValue().get(String.valueOf(id)).getIsProfilePublic() && !getUserAuth().getUserId().equals(id)) {
        return null;
      }
      return profileDtoRedisTemplate.opsForValue().get(String.valueOf(id));
    }
    Optional<User> user = profilePort.takeProfileById(id);

    if (user.isEmpty()) {
      return null;
    }

    Boolean isProfilePublic = user.get().getIsProfilePublic();

    ProfileDto profileDto = ProfileMapper.INSTANCE.userToProfileDto(user.get());
    if (isProfilePublic || user.get().getUserId().equals(getUserAuth().getUserId())) {
      profileDtoRedisTemplate.opsForValue().set(String.valueOf(id), profileDto);
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
      profileDtoRedisTemplate.opsForValue().set(String.valueOf(userId), ProfileMapper.INSTANCE.userToProfileDto(profileDto.get()));
    }
    return profileDtoRedisTemplate.opsForValue().get(String.valueOf(userId));
  }

  @Override
  public ProfileDto setStateProfile(ProfileStateRequest profileStateRequest) {
    Integer state = profileStateRequest.getIsProfilePublic();
    Boolean isSetStateProfile = profilePort.setStateProfileById(state, getUserAuth().getUserId());
    if (isSetStateProfile) {
      ProfileDto profileDto = profileDtoRedisTemplate.opsForValue().get(String.valueOf(getUserAuth().getUserId()));
      if (profileDto != null) {
        profileDto.setIsProfilePublic(state == 1);
      }
      profileDtoRedisTemplate.opsForValue().set(String.valueOf(profileDto.getProfileId()), profileDto);
    }
    return profileDtoRedisTemplate.opsForValue().get(String.valueOf(getUserAuth().getUserId()));
  }

  @Override
  public ProfileDto updateAvatarProfile(ImageDto imageDto) {
    String url = (String) cloudServicePortInput.uploadPictureSetSize(imageDto.getImageUrl(), ImageHandlerPortInput.MAX_SIZE_AVATAR).get("url");
    Boolean check = imageHandlerPort.saveAvatar(url, getUserAuth().getUserId());
    if (check) {
      ProfileDto profileDto = profileDtoRedisTemplate.opsForValue().get(String.valueOf(getUserAuth().getUserId()));
      profileDto.setAvatar(url);
      profileDtoRedisTemplate.opsForValue().set(String.valueOf(profileDto.getProfileId()), profileDto);
      return profileDto;
    } else {
      throw new CustomException("Error updating avatar", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
