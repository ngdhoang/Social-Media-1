package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.ProfilePortInput;
import com.GHTK.Social_Network.application.port.output.AuthPort;
import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ProfileMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService implements ProfilePortInput {
  private final ProfilePort profilePort;

  private final AuthPort authenticationRepositoryPort;

  private User getUserAuth() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
    User user = profilePort.takeProfileById(id);

    if (user == null) {
      return null;
    }

    Boolean isProfilePublic = user.getIsProfilePublic();

    ProfileDto profileDto = ProfileMapper.INSTANCE.userToProfileDto(user);
    if (isProfilePublic || user.getUserId().equals(getUserAuth().getUserId())) {
      return profileDto;
    }
    return null;
  }

  @Override
  public Boolean updateProfile(ProfileDto profileDto) {
    profileDto.setProfileId(getUserAuth().getUserId());
    return profilePort.updateProfile(ProfileMapper.INSTANCE.profileToUser(profileDto));
  }

  @Override
  public Boolean setStateProfile(Integer state) {
    return profilePort.setStateProfileById(state.intValue(), getUserAuth().getUserId());
  }
}
