package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionCountDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionUserDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReactionPostResponseMapper {
  @Mapping(source = "postId", target = "roleId")
  @Mapping(source = "reactionUsers", target = "users")
  @Mapping(source = "reactionCount", target = "reactions")
  ReactionPostResponse toReactionPostResponse(Long postId, List<ReactionUserDto> reactionUsers, List<ReactionCountDto> reactionCount);
}
