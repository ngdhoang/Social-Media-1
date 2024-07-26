package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.Profile;
import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.FieldVisibilityDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface UserMapper {
  @Mapping(source = "userId", target = "userId")
  User UserBasicDtoToUser(UserBasicDto profile);

  @Mapping(source = "userId", target = "userId")
  UserBasicDto userToUserBasicDto(User user);

  UserDto userDtoToUserPublicDto(UserDto user);

  @Mapping(target = "dob", expression = "java(mapFieldVisibility(user.getDob()))")
  @Mapping(target = "phoneNumber", expression = "java(mapFieldVisibility(user.getPhoneNumber()))")
  @Mapping(target = "homeTown", expression = "java(mapFieldVisibility(user.getHomeTown()))")
  @Mapping(target = "schoolName", expression = "java(mapFieldVisibility(user.getSchoolName()))")
  @Mapping(target = "workPlace", expression = "java(mapFieldVisibility(user.getWorkPlace()))")
  @Mapping(target = "gender", expression = "java(mapFieldVisibility(user.getGender()))")
  UserDto userDtoToUserPrivateDto(UserDto user);

//  @Mapping(source = "user.userId", target = "userId")
//  @Mapping(source = "user.firstName", target = "firstName")
//  @Mapping(source = "user.lastName", target = "lastName")
//  @Mapping(source = "user.userEmail", target = "userEmail")
//  @Mapping(source = "user.avatar", target = "avatar")
//  @Mapping(source = "user.background", target = "background")
//  @Mapping(source = "user.isProfilePublic", target = "isProfilePublic")
//  @Mapping(source = "profile.dob", target = "dob", qualifiedByName = "mapDob")
//  @Mapping(source = "profile.phoneNumber", target = "phoneNumber", qualifiedByName = "mapPhoneNumber")
//  @Mapping(source = "profile.homeTown", target = "homeTown", qualifiedByName = "mapHomeTown")
//  @Mapping(source = "profile.schoolName", target = "schoolName", qualifiedByName = "mapSchoolName")
//  @Mapping(source = "profile.workPlace", target = "workPlace", qualifiedByName = "mapWorkPlace")
//  UserDto userAndProfileToUserDtoPrivate(User user, Profile profile);

  @Mapping(target = "dob", source = "profile", qualifiedByName = "mapDob")
  @Mapping(target = "phoneNumber", source = "profile", qualifiedByName = "mapPhoneNumber")
  @Mapping(target = "homeTown", source = "profile", qualifiedByName = "mapHomeTown")
  @Mapping(target = "schoolName", source = "profile", qualifiedByName = "mapSchoolName")
  @Mapping(target = "workPlace", source = "profile", qualifiedByName = "mapWorkPlace")
  @Mapping(target = "gender", source = "profile", qualifiedByName = "mapGender")
  @Mapping(target = "userId", source = "user.userId")
  @Mapping(target = "lastName", source = "user.lastName")
  @Mapping(target = "firstName", source = "user.firstName")
  @Mapping(target = "userEmail", source = "user.userEmail")
  @Mapping(target = "avatar", source = "user.avatar")
  @Mapping(target = "background", source = "user.background")
  @Mapping(target = "isProfilePublic", source = "user.isProfilePublic")
  UserDto userAndProfileToUserDto(User user, Profile profile);

  @Named("mapDob")
  default FieldVisibilityDto<LocalDate> mapDob(Profile profile) {
    return new FieldVisibilityDto<>(profile.getDob(), profile.getIsDobPublic());
  }

  @Named("mapPhoneNumber")
  default FieldVisibilityDto<String> mapPhoneNumber(Profile profile) {
    return new FieldVisibilityDto<>(profile.getPhoneNumber(), profile.getIsPhoneNumberPublic());
  }

  @Named("mapHomeTown")
  default FieldVisibilityDto<String> mapHomeTown(Profile profile) {
    return new FieldVisibilityDto<>(profile.getHomeTown(), profile.getIsHomeTownPublic());
  }

  @Named("mapSchoolName")
  default FieldVisibilityDto<String> mapSchoolName(Profile profile) {
    return new FieldVisibilityDto<>(profile.getSchoolName(), profile.getIsSchoolNamePublic());
  }

  @Named("mapWorkPlace")
  default FieldVisibilityDto<String> mapWorkPlace(Profile profile) {
    return new FieldVisibilityDto<>(profile.getWorkPlace(), profile.getIsWorkPlacePublic());
  }

  @Named("mapGender")
  default FieldVisibilityDto<String> mapGender(Profile profile) {
    return new FieldVisibilityDto<>(profile.getGender() != null ? profile.getGender().name() : null, profile.getIsGenderPublic());
  }
}