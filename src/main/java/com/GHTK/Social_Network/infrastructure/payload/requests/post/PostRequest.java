package com.GHTK.Social_Network.infrastructure.payload.requests.post;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostRequest {
  private Long id;

  private String content;

  private String status = "public";

  private List<Long> tagUserIds = new ArrayList<>();

  private List<Long> imageIds = new ArrayList<>();

  private List<String> publicIds = new ArrayList<>();

  private List<String> deletePublicIds = new ArrayList<>();
}
