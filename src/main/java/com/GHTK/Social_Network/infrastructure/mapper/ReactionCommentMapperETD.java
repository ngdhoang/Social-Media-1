package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.ReactionPost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ReactionPostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReactionCommentMapperETD {
  @Mapping(source = "reactionPostId", target = "reactionPostId")
  @Mapping(source = "reactionType", target = "reactionType")
  ReactionPost toDomain(ReactionPostEntity entity);

  @Mapping(source = "commentId", target = "commentEntity.commentId")
  @Mapping(source = "userId", target = "userEntity.userId")
  @Mapping(source = "reactionType", target = "reactionType")
  ReactionPostEntity toEntity(ReactionPost model);

}