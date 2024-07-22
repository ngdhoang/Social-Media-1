package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
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
  public Optional<UserEntity> takeProfileById(Long id) {
    return Optional.ofNullable(userRepository.findById(id).orElse(null));
  }

  @Override
  public Boolean updateProfile(UpdateProfileRequest updateProfileRequest, Long userId) {
    UserEntity savedUserEntity = userRepository.findById(userId).orElse(null);

    savedUserEntity.setFirstName(updateProfileRequest.getFirstName());
    savedUserEntity.setLastName(updateProfileRequest.getLastName());
    savedUserEntity.setDob(updateProfileRequest.getDob());
    savedUserEntity.setPhoneNumber(updateProfileRequest.getPhoneNumber());
    savedUserEntity.setHomeTown(updateProfileRequest.getHomeTown());
    savedUserEntity.setSchoolName(updateProfileRequest.getSchoolName());
    savedUserEntity.setWorkPlace(updateProfileRequest.getWorkPlace());
    savedUserEntity.setIsProfilePublic(updateProfileRequest.getIsProfilePublic());
    userRepository.save(savedUserEntity);
    return true;
  }

  @Override
  public Boolean setStateProfileById(Integer i, Long userId) {
    Boolean state = i == 1;
    int rowsUpdated = userRepository.changeStateProfile(state, userId);
    return rowsUpdated > 0;
  }
}
