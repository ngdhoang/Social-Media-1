package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.user.Token;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.TokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TokenMapperETD {
  @Mapping(source = "tokenId", target = "tokenId")
  @Mapping(source = "userEntity.userId", target = "userId")
  Token toDomain(TokenEntity tokenEntity);

  @Mapping(source = "tokenId", target = "tokenId")
  @Mapping(source = "userId", target = "userEntity.userId")
  TokenEntity toEntity(Token token);
}
