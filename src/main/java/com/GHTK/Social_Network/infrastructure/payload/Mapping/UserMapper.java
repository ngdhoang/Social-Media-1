package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.Profile;
import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.ProfileEntity;
import com.GHTK.Social_Network.infrastructure.payload.dto.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface UserMapper {
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "oldPassword", ignore = true)
  @Mapping(target = "statusUser", ignore = true)
  @Mapping(target = "role", ignore = true)
  User userDtoToUser(UserDto userDto);

  @Mapping(source = "user.userId", target = "userId")
  @Mapping(source = "user.firstName", target = "firstName")
  @Mapping(source = "user.lastName", target = "lastName")
  @Mapping(source = "user.userEmail", target = "userEmail")
  @Mapping(source = "user.avatar", target = "avatar")
  @Mapping(source = "user.background", target = "background")
  @Mapping(source = "user.isProfilePublic", target = "isProfilePublic")
  @Mapping(source = "profile.dob", target = "dob", qualifiedByName = "mapDob")
  @Mapping(source = "profile.phoneNumber", target = "phoneNumber", qualifiedByName = "mapPhoneNumber")
  @Mapping(source = "profile.homeTown", target = "homeTown", qualifiedByName = "mapHomeTown")
  @Mapping(source = "profile.schoolName", target = "schoolName", qualifiedByName = "mapSchoolName")
  @Mapping(source = "profile.workPlace", target = "workPlace", qualifiedByName = "mapWorkPlace")
  UserDto userAndProfileToUserDtoPublic(User user, Profile profile);

  @Mapping(source = "user.userId", target = "userId")
  @Mapping(source = "user.firstName", target = "firstName")
  @Mapping(source = "user.lastName", target = "lastName")
  @Mapping(source = "user.userEmail", target = "userEmail")
  @Mapping(source = "user.avatar", target = "avatar")
  @Mapping(source = "user.background", target = "background")
  @Mapping(source = "user.isProfilePublic", target = "isProfilePublic")
  @Mapping(source = "profile.dob", target = "dob", qualifiedByName = "mapDob")
  @Mapping(source = "profile.phoneNumber", target = "phoneNumber", qualifiedByName = "mapPhoneNumber")
  @Mapping(source = "profile.homeTown", target = "homeTown", qualifiedByName = "mapHomeTown")
  @Mapping(source = "profile.schoolName", target = "schoolName", qualifiedByName = "mapSchoolName")
  @Mapping(source = "profile.workPlace", target = "workPlace", qualifiedByName = "mapWorkPlace")
  UserDto userAndProfileToUserDtoPrivate(User user, Profile profile);

  @Named("mapDob")
  default LocalDate mapDob(Profile profile) {
    return Boolean.TRUE.equals(profile.getIsDobPublic()) ? profile.getDob() : null;
  }

  @Named("mapPhoneNumber")
  default String mapPhoneNumber(Profile profile) {
    return Boolean.TRUE.equals(profile.getIsPhoneNumberPublic()) ? profile.getPhoneNumber() : null;
  }

  @Named("mapHomeTown")
  default String mapHomeTown(Profile profile) {
    return Boolean.TRUE.equals(profile.getIsHomeTownPublic()) ? profile.getHomeTown() : null;
  }

  @Named("mapSchoolName")
  default String mapSchoolName(Profile profile) {
    return Boolean.TRUE.equals(profile.getIsSchoolNamePublic()) ? profile.getSchoolName() : null;
  }

  @Named("mapWorkPlace")
  default String mapWorkPlace(Profile profile) {
    return Boolean.TRUE.equals(profile.getIsWorkPlacePublic()) ? profile.getWorkPlace() : null;
  }

  @Mapping(target = "password", ignore = true)
  @Mapping(target = "oldPassword", ignore = true)
  @Mapping(target = "statusUser", ignore = true)
  @Mapping(target = "role", ignore = true)
  UserBasicDto userToUserBasicDto(User user);

  @Mapping(target = "password", ignore = true)
  @Mapping(target = "oldPassword", ignore = true)
  @Mapping(target = "statusUser", ignore = true)
  @Mapping(target = "role", ignore = true)
  User userBasicDtoToUser(UserBasicDto userBasicDto);

  Profile profileEntityToProfile(ProfileEntity profileEntity);

  ProfileEntity profileToProfileEntity(Profile profile);

  @Mapping(target = "user", ignore = true)
  void updateProfileEntityFromProfile(Profile profile, @MappingTarget ProfileEntity profileEntity);
}
