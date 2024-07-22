package com.GHTK.Social_Network.domain.model.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReactionPost {
  private Long reactionPostId;

  private Long postId;

  private Long userId;

  private EReactionType reactionType;

  public ReactionPost(Long postId, Long userId, EReactionType reactionType) {
    this.postId = postId;
    this.userId = userId;
    this.reactionType = reactionType;
  }
}
