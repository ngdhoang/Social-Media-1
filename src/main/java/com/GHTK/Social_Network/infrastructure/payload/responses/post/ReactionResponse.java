package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import lombok.Data;

@Data
public class ReactionResponse {
  private Long roleId;

  private Long userId;

  private EReactionType reactionType;
}
