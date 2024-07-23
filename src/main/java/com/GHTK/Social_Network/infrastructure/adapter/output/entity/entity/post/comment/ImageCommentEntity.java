package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "image_comment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageCommentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long imageCommentId;

  private String imageUrl;

  private Date createAt;

  private Boolean isDelete = false;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "comment_id", nullable = false)
  private CommentEntity commentEntity;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;

  public ImageCommentEntity(UserEntity userEntity, CommentEntity commentEntity, String imageUrl, Date createAt) {
    this.userEntity = userEntity;
    this.commentEntity = commentEntity;
    this.imageUrl = imageUrl;
    this.createAt = createAt;
  }
}
