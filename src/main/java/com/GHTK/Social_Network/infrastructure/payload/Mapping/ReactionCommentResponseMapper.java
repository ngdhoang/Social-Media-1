package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionCountDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionUserDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionCommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReactionCommentResponseMapper {
    @Mapping(source = "commentId", target = "roleId")
    @Mapping(source = "reactionUsers", target = "users")
    @Mapping(source = "reactionCount", target = "reactions")
    ReactionCommentResponse toReactionCommentResponse(Long commentId, List<ReactionUserDto> reactionUsers, List<ReactionCountDto> reactionCount);
}
