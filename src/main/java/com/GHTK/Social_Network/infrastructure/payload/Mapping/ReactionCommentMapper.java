package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.ReactionComment;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReactionCommentMapper {
  ReactionCommentMapper INSTANCE = Mappers.getMapper(ReactionCommentMapper.class);

  @Mapping(source = "reactionCommentId", target = "reactionPostId")
  @Mapping(source = "comment.commentId", target = "postId")
  @Mapping(source = "user.userId", target = "userId")
  ReactionResponse toReactionResponse(ReactionComment reactionComment);

  @Mapping(target = "reactionCommentId", ignore = true)
  @Mapping(target = "comment", ignore = true)
  @Mapping(target = "user", ignore = true)
  ReactionComment toReactionComment(ReactionResponse reactionResponse);
}