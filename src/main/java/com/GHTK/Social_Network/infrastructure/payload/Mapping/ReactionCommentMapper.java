package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.ReactionPost;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReactionCommentMapper {
  @Mapping(source = "reactionPostId", target = "roleId")
  ReactionResponse commentToResponse(ReactionPost comment);

  @Mapping(source = "reactionPostId", target = "reactionCommentId")
  ReactionPost responseToComment(ReactionResponse response);
}