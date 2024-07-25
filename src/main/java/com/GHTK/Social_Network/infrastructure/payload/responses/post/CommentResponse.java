package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommentResponse {
  private Long commentId;

  private Date createUp;

  private String content;

  private Long parentCommentId;

  private List<CommentResponse> childComments;

  private Long postId;

  private Long userId;

  private String imageUrl;
}
