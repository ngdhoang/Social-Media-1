package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
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

  private Date createUp;

  private Boolean isDelete = false;

  @Column(columnDefinition = "TEXT")
  private String content;

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

  @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ImageCommentEntity> imageCommentEntities;

  @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ReactionCommentEntity> reactionCommentEntities;

  public CommentEntity(Date createUp, String content, UserEntity userEntity, PostEntity postEntity) {
    this.createUp = createUp;
    this.content = content;
    this.userEntity = userEntity;
    this.postEntity = postEntity;
  }

  public void addChildComment(CommentEntity childCommentEntity) {
    childCommentEntities.add(childCommentEntity);
    childCommentEntity.setParentCommentEntity(this);
  }

}
