package com.GHTK.Social_Network.infrastructure.payload.requests.post;

import lombok.Data;

import java.util.List;

@Data
public class PostRequest {
  private Long id;

  private String content;

  private String status = "public";

  private List<Long> tagUserIds;
}
