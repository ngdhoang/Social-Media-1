package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ReactionPostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReactionPostMapperETD {
  @Mapping(source = "postEntity.postId", target = "postId")
  @Mapping(source = "userEntity.userId", target = "userId")
  ReactionPost toDomain(ReactionPostEntity reactionPostEntity);

  @Mapping(source = "postId", target = "postEntity.postId")
  @Mapping(source = "userId", target = "userEntity.userId")
  ReactionPostEntity toEntity(ReactionPost reactionPost);
}