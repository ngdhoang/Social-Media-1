package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ReactionEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long commentId;

  private LocalDate createAt;

  private String imageUrl;

  @Column(columnDefinition = "TEXT")
  private String content;

  private Long repliesQuantity = 0L;

  private Long reactionsQuantity = 0L;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_comment_id")
  private CommentEntity parentCommentEntity;

  @OneToMany(mappedBy = "parentCommentEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<CommentEntity> childCommentEntities = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "post_id", nullable = false)
  private PostEntity postEntity;

  @OneToMany(mappedBy = "commentEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ReactionEntity> reactionCommentEntities;

  @PrePersist
  public void prePersist() {
    if (this.reactionsQuantity == null) {
      this.reactionsQuantity = 0L;
    }
    if (this.repliesQuantity == null) {
      this.repliesQuantity = 0L;
    }
  }
}

