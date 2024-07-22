package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.entity.user.UserEntity;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProfileMapper {
  ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

  @Mapping(source = "profileId", target = "userId")
  UserEntity profileToUser(ProfileDto profile);

  @Mapping(source = "userId", target = "profileId")
  ProfileDto userToProfileDto(User user);
}