package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "post")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postId;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String content;

  private LocalDate createdAt;

  private LocalDate updateAt;

  private Long reactionsQuantity = 0L;

  private Long commentQuantity = 0L;

  @Enumerated(EnumType.STRING)
  private EPostStatusEntity postStatus;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;

  @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ImagePostEntity> imagePostEntities;

  @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<TagUserEntity> tagUserEntities;

  @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ReactionPostEntity> reactionPostEntities;

  @OneToMany(mappedBy = "postEntity", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<CommentEntity> commentEntities;

  public void addReaction(ReactionPostEntity reaction) {
    reactionPostEntities.add(reaction);
    reaction.setPostEntity(this);
    this.reactionsQuantity++;
  }

  public void removeReaction(ReactionPostEntity reaction) {
    reactionPostEntities.remove(reaction);
    reaction.setPostEntity(null);
    this.reactionsQuantity--;
  }

  public void addComment(CommentEntity comment) {
    commentEntities.add(comment);
    comment.setPostEntity(this);
    this.commentQuantity++;
  }

  public void removeComment(CommentEntity comment) {
    commentEntities.remove(comment);
    comment.setPostEntity(null);
    this.commentQuantity--;
  }
}
