package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PostResponse {
  private Long postId;

  private String content;

  private Date createdAt;

  private Date updateAt;

  private List<ImageDto> imagePosts;
}
