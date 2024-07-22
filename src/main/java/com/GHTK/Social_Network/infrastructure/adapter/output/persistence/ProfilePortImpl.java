package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.MapperEntity.UserMapper;
import com.GHTK.Social_Network.infrastructure.entity.user.UserEntity;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateProfileRequest;
import com.GHTK.Social_Network.infrastructure.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfilePortImpl implements ProfilePort {
  private final UserEntityRepository userEntityRepository;

  @Override
  public Optional<User> takeProfileById(Long id) {
    return Optional.ofNullable(UserMapper.INSTANCE.toDomain(userEntityRepository.findById(id).orElse(null)));
  }

  @Override
  public Boolean updateProfile(UpdateProfileRequest updateProfileRequest, Long userId) {
    UserEntity savedUserEntity = userEntityRepository.findById(userId).orElse(null);

    savedUserEntity.setFirstName(updateProfileRequest.getFirstName());
    savedUserEntity.setLastName(updateProfileRequest.getLastName());
    savedUserEntity.setDob(updateProfileRequest.getDob());
    savedUserEntity.setPhoneNumber(updateProfileRequest.getPhoneNumber());
    savedUserEntity.setHomeTown(updateProfileRequest.getHomeTown());
    savedUserEntity.setSchoolName(updateProfileRequest.getSchoolName());
    savedUserEntity.setWorkPlace(updateProfileRequest.getWorkPlace());
    savedUserEntity.setIsProfilePublic(updateProfileRequest.getIsProfilePublic());
    userEntityRepository.save(savedUserEntity);
    return null;
  }

  @Override
  public Boolean setStateProfileById(Integer i, Long userId) {
    Boolean state = i == 1;
    int rowsUpdated = userEntityRepository.changeStateProfile(state, userId);
    return rowsUpdated > 0;
  }
}
