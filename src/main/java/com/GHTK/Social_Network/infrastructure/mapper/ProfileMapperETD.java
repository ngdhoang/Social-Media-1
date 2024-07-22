package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProfileMapperETD {
  @Mapping(source = "profileId", target = "userId")
  User toDomain(ProfileDto profile);

  @Mapping(source = "userId", target = "profileId")
  UserEntity toEntity(User user);
}