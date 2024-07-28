package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ReactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReactionPostMapperETD {
  @Mapping(source = "postEntity.postId", target = "postId")
  @Mapping(source = "commentEntity.commentId", target = "commentId")
  @Mapping(source = "userEntity.userId", target = "userId")
  ReactionPost toDomain(ReactionEntity reactionEntity);

  @Mapping(source = "postId", target = "postEntity.postId")
  @Mapping(source = "commentId", target = "commentEntity.commentId")
  @Mapping(source = "userId", target = "userEntity.userId")
  ReactionEntity toEntity(ReactionPost reactionPost);
}
