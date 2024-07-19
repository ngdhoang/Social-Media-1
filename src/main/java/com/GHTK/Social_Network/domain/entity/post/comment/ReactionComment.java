package com.GHTK.Social_Network.domain.entity.post.comment;

import com.GHTK.Social_Network.domain.entity.post.EReactionType;
import jakarta.persistence.*;

@Entity
public class ReactionComment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reactionCommentId;

  @Enumerated(EnumType.STRING)
  private EReactionType reactionType;
}
