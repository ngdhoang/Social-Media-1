package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReactionCommentMapper {
  @Mapping(source = "reactionId", target = "roleId")
  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "reactionType", target = "reactionType")
  ReactionResponse commentToResponse(ReactionPost reactionPost);

  @Mapping(source = "roleId", target = "reactionId")
  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "reactionType", target = "reactionType")
  @Mapping(target = "postId", ignore = true)
  @Mapping(target = "commentId", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updateAt", ignore = true)
  ReactionPost responseToComment(ReactionResponse response);
}