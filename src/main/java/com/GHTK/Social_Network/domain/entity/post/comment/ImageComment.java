package com.GHTK.Social_Network.domain.entity.post.comment;

import com.GHTK.Social_Network.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageComment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long imageCommentId;

  private String imageUrl;

  private Date createAt;

  private Boolean isDelete = false;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "comment_id", nullable = false)
  private Comment comment;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public ImageComment(User user, Comment comment, String imageUrl, Date createAt) {
    this.user = user;
    this.comment = comment;
    this.imageUrl = imageUrl;
    this.createAt = createAt;
  }
}
