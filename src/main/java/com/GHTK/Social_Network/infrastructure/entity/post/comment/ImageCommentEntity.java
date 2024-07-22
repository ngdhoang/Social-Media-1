package com.GHTK.Social_Network.infrastructure.entity.post.comment;

import com.GHTK.Social_Network.infrastructure.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
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
  private CommentEntity comment;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;

  public ImageCommentEntity(UserEntity userEntity, CommentEntity comment, String imageUrl, Date createAt) {
    this.userEntity = userEntity;
    this.comment = comment;
    this.imageUrl = imageUrl;
    this.createAt = createAt;
  }
}
