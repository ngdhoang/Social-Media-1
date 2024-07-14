package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfilePortImpl implements ProfilePort {
  private final UserRepository userRepository;

  @Override
  public User takeProfileById(Long id) {
    return userRepository.findById(id).orElse(null);
  }

  @Override
  public Boolean updateProfile(User user) {
    User savedUser = userRepository.findById(user.getUserId()).orElse(null);
    if (savedUser != null) {
      savedUser.setFirstName(user.getFirstName());
      savedUser.setLastName(user.getLastName());
      savedUser.setDob(user.getDob());
      savedUser.setPhoneNumber(user.getPhoneNumber());
      savedUser.setHomeTown(user.getHomeTown());
      savedUser.setSchoolName(user.getSchoolName());
      savedUser.setWorkPlace(user.getWorkPlace());
      savedUser.setIsProfilePublic(user.getIsProfilePublic());
      userRepository.save(savedUser);
      return true;
    }
    return false;
  }

  @Override
  public Boolean setStateProfileById(Integer i, Long userId) {
    Boolean state = i == 1;
    try {
      userRepository.changeStateProfile(state, userId);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
