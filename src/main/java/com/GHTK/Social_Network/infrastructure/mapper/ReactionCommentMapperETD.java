package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.ReactionComment;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.ReactionCommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReactionCommentMapperETD {
  @Mapping(source = "commentEntity.commentId", target = "commentId")
  @Mapping(source = "userEntity.userId", target = "userId")
  @Mapping(source = "reactionType", target = "reactionType")
  ReactionComment toDomain(ReactionCommentEntity entity);

  @Mapping(source = "commentId", target = "commentEntity.commentId")
  @Mapping(source = "userId", target = "userEntity.userId")
  @Mapping(source = "reactionType", target = "reactionType")
  ReactionCommentEntity toEntity(ReactionComment model);
}