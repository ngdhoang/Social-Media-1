package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProfileMapperETD {
  @Mapping(source = "userId", target = "userId")
  User toDomain(User user);

  @Mapping(source = "userId", target = "userId")
  UserEntity toEntity(User user);
}