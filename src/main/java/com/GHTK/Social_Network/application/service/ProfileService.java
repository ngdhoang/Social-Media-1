package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.ProfilePortInput;
import com.GHTK.Social_Network.application.port.output.AuthPort;
import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ProfileMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService implements ProfilePortInput {
  private final ProfilePort profilePort;

  private final AuthPort authenticationRepositoryPort;

  private final RedisTemplate<String, ProfileDto> profileDtoRedisTemplate;

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
    if (Boolean.TRUE.equals(profileDtoRedisTemplate.hasKey(String.valueOf(id)))) {
      if (!profileDtoRedisTemplate.opsForValue().get(String.valueOf(id)).getIsProfilePublic() && !getUserAuth().getUserId().equals(id)) {
        return null;
      }
      return profileDtoRedisTemplate.opsForValue().get(String.valueOf(id));
    }
    User user = profilePort.takeProfileById(id);

    if (user == null) {
      return null;
    }

    Boolean isProfilePublic = user.getIsProfilePublic();

    ProfileDto profileDto = ProfileMapper.INSTANCE.userToProfileDto(user);
    if (isProfilePublic || user.getUserId().equals(getUserAuth().getUserId())) {
      profileDtoRedisTemplate.opsForValue().set(String.valueOf(id), profileDto);
      return profileDto;
    }
    return null;
  }

  @Override
  public Boolean updateProfile(ProfileDto profileDto) {
    profileDto.setProfileId(getUserAuth().getUserId());
    Boolean isUpdateProfile = profilePort.updateProfile(ProfileMapper.INSTANCE.profileToUser(profileDto));
    if (isUpdateProfile) {
      profileDtoRedisTemplate.opsForValue().set(String.valueOf(profileDto.getProfileId()), profileDto);
    }
    return isUpdateProfile;
  }

  @Override
  public Boolean setStateProfile(Integer state) {
    Boolean isSetStateProfile = profilePort.setStateProfileById(state, getUserAuth().getUserId());
    if (isSetStateProfile) {
      ProfileDto profileDto = profileDtoRedisTemplate.opsForValue().get(String.valueOf(getUserAuth().getUserId()));
      if (profileDto != null) {
        profileDto.setIsProfilePublic(state == 1);
      }
      profileDtoRedisTemplate.opsForValue().set(String.valueOf(profileDto.getProfileId()), profileDto);
    }
    return isSetStateProfile;
  }
}
