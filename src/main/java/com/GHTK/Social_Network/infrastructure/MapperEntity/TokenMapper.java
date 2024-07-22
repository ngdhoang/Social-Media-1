package com.GHTK.Social_Network.infrastructure.MapperEntity;

import com.GHTK.Social_Network.domain.model.user.Token;
import com.GHTK.Social_Network.infrastructure.entity.user.TokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TokenMapper {
  TokenMapper INSTANCE = Mappers.getMapper(TokenMapper.class);

  @Mapping(source = "userEntity.userId", target = "userId")
  Token toDomain(TokenEntity tokenEntity);

  @Mapping(source = "userId", target = "userEntity.userId")
  TokenEntity toEntity(Token token);
}
