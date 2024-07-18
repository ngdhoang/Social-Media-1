package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class PostResponse {
  private Long postId;

  private String content;

  private Date createdAt;

  private Date updateAt;

  private List<String> imagePost;
}
