package com.GHTK.Social_Network.infrastructure.entity.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImagePostEntity {
  private Long imagePostId;

  private String imageUrl;

  private Date createAt;

  private Long postId;

  public ImagePostEntity(String imageUrl, Date createAt, PostEntity postEntity) {
    this.imageUrl = imageUrl;
    this.createAt = createAt;
    this.postId = postEntity.getPostId();
  }
}
