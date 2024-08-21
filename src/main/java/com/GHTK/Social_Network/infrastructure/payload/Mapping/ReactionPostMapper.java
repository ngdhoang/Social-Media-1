package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.ReactionPost;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.responses.ActivityInteractionResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface ReactionPostMapper {
  ReactionResponse postToResponse(ReactionPost post);

  @Mapping(target = "createAt", ignore = true)
  @Mapping(target = "updateAt", ignore = true)
  ReactionPost responseToPost(ReactionResponse response);

  @Mapping(target = "owner", source = "user")
  @Mapping(target = "roleId", source = "reactionId")
  @Mapping(target = "post", source = "post")
  @Mapping(target = "role", source = "role")
  @Mapping(target = "createAt", source = "createAt")
  @Mapping(target = "parentCommentId", source = "parentCommentId")
  @Mapping(target = "imageUrl", source = "imageUrl")
  @Mapping(target = "reactionType", source = "eReactionType")
  ActivityInteractionResponse reactionToReactionActivityResponse(Long reactionId, EReactionType eReactionType, String imageUrl, Instant createAt, Long parentCommentId, User user, Post post, String role);

}