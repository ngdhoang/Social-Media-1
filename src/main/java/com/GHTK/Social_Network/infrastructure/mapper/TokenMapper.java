package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.Token;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.TokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TokenMapper {
  @Mapping(source = "tokenId", target = "tokenId")
  @Mapping(source = "user.userId", target = "userID")
  Token toDomain(TokenEntity tokenEntity);

  @Mapping(source = "tokenId", target = "tokenId")
  @Mapping(source = "userId", target = "user.userID")
  TokenEntity toEntity(Token token);
}
