package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionPost {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reactionPostId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;

  @Enumerated(EnumType.STRING)
  private EReactionType reactionType;

  public ReactionPost(Post post, UserEntity userEntity, EReactionType reactionType) {
    this.post = post;
    this.userEntity = userEntity;
    this.reactionType = reactionType;
  }
}
