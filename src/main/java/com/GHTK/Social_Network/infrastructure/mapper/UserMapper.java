package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
  UserEntity toEntity(User user);

  User toDomain(UserEntity userEntity);
}
