package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.ReactionComment;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ReactionCommentMapper {
  @Mapping(source = "reactionCommentId", target = "reactionPostId")
  @Mapping(source = "commentId", target = "postId")
  ReactionResponse commentToResponse(ReactionComment comment);

  @Mapping(source = "reactionPostId", target = "reactionCommentId")
  @Mapping(source = "postId", target = "commentId")
  ReactionComment responseToComment(ReactionResponse response);
}