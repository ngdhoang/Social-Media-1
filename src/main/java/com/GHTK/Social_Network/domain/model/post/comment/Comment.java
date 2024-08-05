package com.GHTK.Social_Network.domain.model.post.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

  private Long repliesQuantity;

  private Long reactionsQuantity;

  public Comment(LocalDate createAt, String content, Long userId, Long postId, String imageUrl) {
    this.createAt = createAt;
    this.content = content;
    this.userId = userId;
    this.postId = postId;
    this.imageUrl = imageUrl;
  }
}
