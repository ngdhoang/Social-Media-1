package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProfileMapper {
  ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

  @Mapping(source = "userId", target = "profileId")
  User profileToUser(ProfileDto profile);

  @Mapping(source = "profileId", target = "userId")
  ProfileDto userToProfileDto(User user);
}
