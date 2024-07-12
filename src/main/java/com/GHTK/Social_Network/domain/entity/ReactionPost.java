package com.GHTK.Social_Network.domain.entity;

import jakarta.persistence.*;

public class ReactionPost {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reactionPostId;

  @Enumerated(EnumType.STRING)
  private EReactionType reactionType;
}
