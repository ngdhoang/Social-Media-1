package com.GHTK.Social_Network.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageComment {
  private Long imageCommentId;

  private String imageUrl;

  private Date createAt;

  private Long commentId;

  private Long userId;

  public ImageComment(Long userId, Long commentId, String imageUrl, Date createAt) {
    this.userId = userId;
    this.commentId = commentId;
    this.imageUrl = imageUrl;
    this.createAt = createAt;
  }
}
