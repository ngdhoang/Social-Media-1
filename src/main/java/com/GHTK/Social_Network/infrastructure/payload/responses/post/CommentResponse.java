package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
  private UserBasicDto user;

  private Long commentId;

  private Long postId;

  private LocalDate createAt;

  private String content;

  private Long parentCommentId;

  private String imageUrl;

  private Long repliesQuantity;

  private Long reactionsQuantity;
}
