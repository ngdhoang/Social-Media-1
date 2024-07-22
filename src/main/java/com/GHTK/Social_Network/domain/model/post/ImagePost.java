package com.GHTK.Social_Network.domain.model.post;

import jakarta.persistence.PrePersist;
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

  @PrePersist
  public void prePersist() {
    createAt = new Date();
  }

  public ImagePost(String imageUrl, Date createAt, Long postID) {
    this.imageUrl = imageUrl;
    this.createAt = createAt;
    this.postId = postId;
  }
}
