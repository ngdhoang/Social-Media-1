package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.user;

import com.GHTK.Social_Network.application.port.output.ProfilePort;

import com.GHTK.Social_Network.domain.model.user.Profile;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.ProfileEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.HometownNode;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.UserNode;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ProfileRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.node.UserNodeRepository;
import com.GHTK.Social_Network.infrastructure.mapper.UserMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.requests.profile.UpdateProfileRequest;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.ProfileStateDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileAdapter implements ProfilePort {
  private final UserRepository userRepository;
  private final ProfileRepository profileRepository;
  private final UserNodeRepository userNodeRepository;

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
    Integer homeTown = updateProfileRequest.getHomeTown();
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

    HometownNode hometownNode = userNodeRepository.getUserWithHometown(userId);
    if (hometownNode != null){
      if (homeTown != null && !Objects.equals(hometownNode.getHometownId(), homeTown)) {
        userNodeRepository.removeUserHometown(userId);
        userNodeRepository.setUserHometown(userId, homeTown);
      }
      else if (homeTown == null){
        userNodeRepository.removeUserHometown(userId);
      }
    } else if (hometownNode == null && homeTown != null) {
      userNodeRepository.setUserHometown(userId, homeTown);
    }

    return true;
  }

  @Override
  public User setProfilePrivacyById(Boolean state, Long userId) {
    userRepository.changeStateProfile(state, userId);
    return userMapperETD.toDomain(userRepository.findById(userId).orElse(null));
  }

  @Override
  public Boolean setProfileStateById(ProfileStateDto profileStateDto, Long userId) {
    int updatedRows = profileRepository.updateProfilePrivacy(
            profileStateDto.getIsDobPublic(),
            profileStateDto.getIsPhoneNumberPublic(),
            profileStateDto.getIsHomeTownPublic(),
            profileStateDto.getIsSchoolNamePublic(),
            profileStateDto.getIsWorkPlacePublic(),
            userId
    );
    return updatedRows == 1;
  }

  @Override
  public String saveAvatar(String avatar, Long id) {
    String imageOld = Objects.requireNonNull(userRepository.findById(id).orElse(null)).getAvatar();
    boolean check = userRepository.changeAvatar(avatar, id) == 1;
    return !check ? imageOld : avatar;
  }

  @Override
  public String saveBackground(String avatar, Long id) {
    String imageOld = Objects.requireNonNull(userRepository.findById(id).orElse(null)).getBackground();
    boolean check = userRepository.changeBackground(avatar, id) == 1;
    return !check ? imageOld : avatar;
  }
}
