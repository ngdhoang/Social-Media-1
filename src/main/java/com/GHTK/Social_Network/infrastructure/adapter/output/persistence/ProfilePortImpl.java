package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfilePortImpl implements ProfilePort {
  private final UserRepository userRepository;

  @Override
  public Optional<User> takeProfileById(Long id) {
    return Optional.ofNullable(userRepository.findById(id).orElse(null));
  }

  @Override
  public Boolean updateProfile(UpdateProfileRequest updateProfileRequest, Long userId) {
    User savedUser = userRepository.findById(userId).orElse(null);
    if (savedUser != null) {
      savedUser.setFirstName(updateProfileRequest.getFirstName());
      savedUser.setLastName(updateProfileRequest.getLastName());
      savedUser.setDob(updateProfileRequest.getDob());
      savedUser.setPhoneNumber(updateProfileRequest.getPhoneNumber());
      savedUser.setHomeTown(updateProfileRequest.getHomeTown());
      savedUser.setSchoolName(updateProfileRequest.getSchoolName());
      savedUser.setWorkPlace(updateProfileRequest.getWorkPlace());
      savedUser.setIsProfilePublic(updateProfileRequest.getIsProfilePublic());
      userRepository.save(savedUser);
      return true;
    }
    return false;
  }

  @Override
  public Boolean setStateProfileById(Integer i, Long userId) {
    Boolean state = i == 1;
    int rowsUpdated = userRepository.changeStateProfile(state, userId);
    return rowsUpdated > 0;
  }
}
