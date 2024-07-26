package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import com.GHTK.Social_Network.infrastructure.payload.dto.UserBasicDto;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommentResponse {
  private UserBasicDto user;

  private Long commentId;

  private Date createUp;

  private String content;

  private Long parentCommentId;

  private List<CommentResponse> childComments;

  private Long postId;

  private String imageUrl;
}
