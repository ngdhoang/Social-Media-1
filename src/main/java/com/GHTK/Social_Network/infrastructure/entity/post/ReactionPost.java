package com.GHTK.Social_Network.infrastructure.entity.post;

import com.GHTK.Social_Network.infrastructure.entity.user.UserEntity;
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
  private PostEntity postEntity;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;

  @Enumerated(EnumType.STRING)
  private EReactionType reactionType;

  public ReactionPost(PostEntity postEntity, UserEntity userEntity, EReactionType reactionType) {
    this.postEntity = postEntity;
    this.userEntity = userEntity;
    this.reactionType = reactionType;
  }
}
