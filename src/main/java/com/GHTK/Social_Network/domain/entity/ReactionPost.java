package com.GHTK.Social_Network.domain.entity;

import com.GHTK.Social_Network.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ReactionPost {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reactionPostId;

  @Enumerated(EnumType.STRING)
  private EReactionType reactionType;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "postId", nullable = false)
  private Post post;
}
