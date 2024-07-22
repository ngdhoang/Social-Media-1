package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EReactionType;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReactionComment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reactionCommentId;

  @Enumerated(EnumType.STRING)
  private EReactionType reactionType;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "comment_id", nullable = false)
  private Comment comment;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;

  public ReactionComment(EReactionType reactionType, Comment comment, UserEntity userEntity) {
    this.reactionType = reactionType;
    this.comment = comment;
    this.userEntity = userEntity;
  }
}
