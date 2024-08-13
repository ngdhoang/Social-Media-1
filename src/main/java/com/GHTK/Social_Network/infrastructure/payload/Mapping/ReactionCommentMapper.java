package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.post.ReactionComment;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReactionCommentMapper {
  @Mapping(source = "reactionCommentId", target = "roleId")
  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "reactionType", target = "reactionType")
  ReactionResponse commentToResponse(ReactionComment reactionComment);
}