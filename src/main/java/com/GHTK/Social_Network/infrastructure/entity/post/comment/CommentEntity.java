package com.GHTK.Social_Network.infrastructure.entity.post.comment;

import com.GHTK.Social_Network.infrastructure.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
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
  private CommentEntity parentComment;

  @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<CommentEntity> childComments = new ArrayList<>();
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "post_id", nullable = false)
  private PostEntity postEntity;

  @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY,
          cascade = CascadeType.ALL)
  private List<ImageCommentEntity> imageComments;

  public CommentEntity(Date createUp, String content, UserEntity userEntity, PostEntity postEntity) {
    this.createUp = createUp;
    this.content = content;
    this.userEntity = userEntity;
    this.postEntity = postEntity;
  }

  public void addChildComment(CommentEntity childComment) {
    childComments.add(childComment);
    childComment.setParentComment(this);
  }

}
