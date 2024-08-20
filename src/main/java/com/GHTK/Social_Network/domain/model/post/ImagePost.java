package com.GHTK.Social_Network.domain.model.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImagePost {
  private Long imagePostId;

  private String imageUrl;

  private Date createAt;

  private Long postId;

  public ImagePost(String imageUrl, Date createAt, Long postId) {
    this.imageUrl = imageUrl;
    this.createAt = createAt;
    this.postId = postId;
  }
}
