package com.GHTK.Social_Network.domain.model.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionPost {
  private Long reactionId;

  private Long postId;

  private Long commentId;

  private EReactionType reactionType;

  private Long userId;

  private LocalDate createdAt;

  private LocalDate updateAt;

  public ReactionPost(EReactionType reactionType, Long commentId, Long userId) {
    this.reactionType = reactionType;
    this.commentId = commentId;
    this.userId = userId;
  }
}
