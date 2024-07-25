package com.GHTK.Social_Network.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReactionComment {
  private Long reactionCommentId;

  private EReactionType reactionType;

  private Long commentId;

  private Long userId;

  public ReactionComment(EReactionType reactionType, Long commentId, Long userId) {
    this.reactionType = reactionType;
    this.commentId = commentId;
    this.userId = userId;
  }
}
