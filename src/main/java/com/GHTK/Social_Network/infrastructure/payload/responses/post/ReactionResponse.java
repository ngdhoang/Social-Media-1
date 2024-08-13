package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReactionResponse {
  private Long roleId;

  private Long userId;

  private EReactionType reactionType;
}
