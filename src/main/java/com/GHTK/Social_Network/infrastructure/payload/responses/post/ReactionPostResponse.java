package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import com.GHTK.Social_Network.domain.entity.post.EReactionType;
import lombok.Data;

@Data
public class ReactionPostResponse {
  private Long reactionPostId;

  private Long postId;

  private Long userId;

  private EReactionType reactionType;
}
