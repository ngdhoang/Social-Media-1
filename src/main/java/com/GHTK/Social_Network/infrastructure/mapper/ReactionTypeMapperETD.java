package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EReactionTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReactionTypeMapperETD {
  EReactionType toDomain(EReactionTypeEntity eReactionTypeEntity);

  EReactionTypeEntity toEntity(EReactionType eReactionType);
}
