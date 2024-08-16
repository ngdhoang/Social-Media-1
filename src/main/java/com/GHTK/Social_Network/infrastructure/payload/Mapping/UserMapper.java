package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.user.Profile;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.FieldVisibilityDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

  @Named("mapFieldVisibility")
  default <T> FieldVisibilityDto<T> mapFieldVisibility(FieldVisibilityDto<T> field) {
    FieldVisibilityDto<T> dto;
    if (field != null) {
      if (!field.isVisibility()) {
        dto = new FieldVisibilityDto<>(null, false);
      } else {
        dto = new FieldVisibilityDto<>(field.getValue(), true);
      }
      return dto;
    }
    return null;
  }

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
  default FieldVisibilityDto<String> mapDob(Profile profile) {
    if(profile.getDob() == null) {
      return new FieldVisibilityDto<>(null, false);
    }
    DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return new FieldVisibilityDto<>(profile.getDob().format(formatter), profile.getIsDobPublic());
  }

  @Named("mapPhoneNumber")
  default FieldVisibilityDto<String> mapPhoneNumber(Profile profile) {
    return new FieldVisibilityDto<>(profile.getPhoneNumber(), profile.getIsPhoneNumberPublic());
  }

  @Named("mapHomeTown")
  default FieldVisibilityDto<Integer> mapHomeTown(Profile profile) {
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