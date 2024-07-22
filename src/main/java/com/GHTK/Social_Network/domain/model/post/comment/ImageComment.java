package com.GHTK.Social_Network.domain.model.post.comment;

import com.GHTK.Social_Network.domain.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageComment {
  private Long imageCommentId;

  private String imageUrl;

  private Date createAt;

  private Boolean isDelete = false;

  private Comment comment;

  private User user;

  public ImageComment(User user, Comment comment, String imageUrl, Date createAt) {
    this.user = user;
    this.comment = comment;
    this.imageUrl = imageUrl;
    this.createAt = createAt;
  }
}
