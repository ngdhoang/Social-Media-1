package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import java.util.Date;
import java.util.List;

public class PostResponse {
  private Long postId;

  private String content;

  private Date createdAt;

  private Date updateAt;

  private List<String> imagePost;
}
