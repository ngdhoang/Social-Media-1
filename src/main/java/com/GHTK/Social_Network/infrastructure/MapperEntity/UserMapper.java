package com.GHTK.Social_Network.infrastructure.MapperEntity;

import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.entity.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserEntity toEntity(User user);

  User toDomain(UserEntity userEntity);
}
