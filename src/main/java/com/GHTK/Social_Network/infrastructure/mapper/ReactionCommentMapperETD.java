package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ReactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReactionCommentMapperETD {
  @Mapping(source = "reactionId", target = "reactionId")
  @Mapping(source = "reactionType", target = "reactionType")
  ReactionPost toDomain(ReactionEntity entity);

  @Mapping(source = "commentId", target = "commentEntity.commentId")
  @Mapping(source = "userId", target = "userEntity.userId")
  @Mapping(source = "reactionType", target = "reactionType")
  ReactionEntity toEntity(ReactionPost model);

}