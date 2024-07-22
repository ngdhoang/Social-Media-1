package com.GHTK.Social_Network.infrastructure.entity.post.comment;

import com.GHTK.Social_Network.infrastructure.entity.post.EReactionType;
import jakarta.persistence.*;

@Entity
public class ReactionCommentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reactionCommentId;

  @Enumerated(EnumType.STRING)
  private EReactionType reactionType;
}
