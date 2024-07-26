package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.domain.model.Profile;
import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.ProfileEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ProfileRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.mapper.UserMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.requests.profile.UpdateProfileRequest;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileStateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileAdapter implements ProfilePort {
  private final UserRepository userRepository;
  private final ProfileRepository profileRepository;

  private final UserMapperETD userMapperETD;

  @Override
  public Optional<User> takeUserById(Long id) {
    return Optional.ofNullable(userMapperETD.toDomain(userRepository.findById(id).orElse(null)));
  }

  @Override
  public Profile takeProfileById(Long id) {
    return userMapperETD.toDomain(profileRepository.findById(id).orElse(null));
  }

  @Override
  public Boolean updateProfile(UpdateProfileRequest updateProfileRequest, Long userId) {
    UserEntity savedUserEntity = userRepository.findById(userId).orElse(null);
    ProfileEntity saveProfileEntity = profileRepository.findById(userId).orElse(null);
    if (savedUserEntity == null || saveProfileEntity == null) {
      return false;
    }
    savedUserEntity.setFirstName(updateProfileRequest.getFirstName());
    savedUserEntity.setLastName(updateProfileRequest.getLastName());
    savedUserEntity.setIsProfilePublic(updateProfileRequest.getIsProfilePublic());

    saveProfileEntity.setDob(updateProfileRequest.getDob());
    saveProfileEntity.setPhoneNumber(updateProfileRequest.getPhoneNumber());
    saveProfileEntity.setHomeTown(updateProfileRequest.getHomeTown());
    saveProfileEntity.setSchoolName(updateProfileRequest.getSchoolName());
    saveProfileEntity.setWorkPlace(updateProfileRequest.getWorkPlace());

    userRepository.save(savedUserEntity);
    return true;
  }

  @Override
  public Boolean setProfilePrivacyById(Boolean state, Long userId) {
    int rowsUpdated = userRepository.changeStateProfile(state, userId);
    return rowsUpdated > 0;
  }

  @Override
  public Boolean setProfileStateById(ProfileStateDto profileStateDto, Long userId) {
    ProfileEntity profileEntity = ProfileEntity.builder()
            .isHomeTownPublic(profileStateDto.isHomeTownPublic())
            .isDobPublic(profileStateDto.isDobPublic())
            .isSchoolNamePublic(profileStateDto.isSchoolNamePublic())
            .isGenderPublic(profileStateDto.isGenderPublic())
            .isWorkPlacePublic(profileStateDto.isWorkPlacePublic())
            .build();
    return profileRepository.updateProfilePrivacy(profileEntity, userId) == 1;
  }

  @Override
  public String saveAvatar(String avatar, Long id) {
    String imageOld = userRepository.findById(id).orElse(null).getAvatar();
    boolean check = userRepository.changeAvatar(avatar, id) == 1;
    return check ? imageOld : null;
  }

  @Override
  public String saveBackground(String avatar, Long id) {
    String imageOld = userRepository.findById(id).orElse(null).getBackground();
    boolean check = userRepository.changeBackground(avatar, id) == 1;
    return check ? imageOld : null;
  }
}
