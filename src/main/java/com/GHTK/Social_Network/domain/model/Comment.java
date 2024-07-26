package com.GHTK.Social_Network.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
  private Long commentId;

  private LocalDate createAt;

  private String imageUrl;

  private String content;

  private Long parentCommentId;

  private Long userId;

  private Long postId;

  public Comment(LocalDate createAt, String content, Long userId, Long postId, String imageUrl) {
    this.createAt = createAt;
    this.content = content;
    this.userId = userId;
    this.postId = postId;
    this.imageUrl = imageUrl;
  }
}
