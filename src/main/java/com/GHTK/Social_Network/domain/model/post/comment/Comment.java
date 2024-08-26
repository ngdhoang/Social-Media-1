package com.GHTK.Social_Network.domain.model.post.comment;

import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
  private Long commentId;

  private Instant createAt;

  private String imageUrl;

  private String content;

  private Long parentCommentId;

  private Long userId;

  private Long postId;

  private Long repliesQuantity;

  private Long reactionsQuantity;

  public Comment(String content, Long userId, Long postId, String imageUrl) {
    this.content = content;
    this.userId = userId;
    this.postId = postId;
    this.imageUrl = imageUrl;
  }

  public Comment(String content, Long userId,Long parentCommentId, Long postId, String imageUrl) {
    this.content = content;
    this.userId = userId;
    this.postId = postId;
    this.imageUrl = imageUrl;
    this.parentCommentId = parentCommentId;
  }

  @PrePersist
  public void prePersist() {
    createAt = Instant.now();
  }

}
