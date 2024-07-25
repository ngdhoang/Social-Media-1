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
public class Comment {
  private Long commentId;

  private Date createUp;

  private String imageUrl;

  private String content;

  private Long parentCommentId;

  private Long userId;

  private Long postId;

  public Comment(Date createUp, String content, Long userId, Long postId, String imageUrl) {
    this.createUp = createUp;
    this.content = content;
    this.userId = userId;
    this.postId = postId;
    this.imageUrl = imageUrl;
  }


}
