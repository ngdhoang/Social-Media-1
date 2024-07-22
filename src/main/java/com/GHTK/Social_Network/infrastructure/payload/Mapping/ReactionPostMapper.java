package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.infrastructure.entity.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReactionPostMapper {
  ReactionPostMapper INSTANCE = Mappers.getMapper(ReactionPostMapper.class);

  @Mapping(source = "reactionPostId", target = "reactionPostId")
  @Mapping(source = "post.postId", target = "postId")
  @Mapping(source = "userEntity.userId", target = "userId")
  ReactionPostResponse toReactionPostResponse(ReactionPost reactionPost);
}