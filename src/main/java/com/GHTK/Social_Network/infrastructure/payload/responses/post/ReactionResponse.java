package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import com.GHTK.Social_Network.domain.model.EReactionType;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EReactionTypeEntity;
import lombok.Data;

@Data
public class ReactionResponse {
  private Long reactionPostId;

  private Long postId;

  private Long userId;

  private EReactionType reactionType;
}
