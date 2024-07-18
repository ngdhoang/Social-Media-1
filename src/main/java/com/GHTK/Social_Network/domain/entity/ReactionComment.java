package com.GHTK.Social_Network.domain.entity;

import com.GHTK.Social_Network.domain.entity.post.EReactionType;
import jakarta.persistence.*;

public class ReactionComment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reactionCommentId;

  @Enumerated(EnumType.STRING)
  private EReactionType reactionType;
}
