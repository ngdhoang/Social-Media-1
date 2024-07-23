package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionPostCountDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionPostUserDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ReactionPostResponseMapper {
  @Mapping(source = "postId", target = "postId")
  @Mapping(source = "reactionUsers", target = "reactionUsers")
  @Mapping(source = "reactionCount", target = "reactionCount")
  ReactionPostResponse toReactionPostResponse(Long postId, List<ReactionPostUserDto> reactionUsers, List<ReactionPostCountDto> reactionCount);
}
