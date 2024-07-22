package com.GHTK.Social_Network.domain.model.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
  private Long postId;

  private String content;

  private Date createdAt;

  private Date updateAt;

  private EPostStatus postStatus;

  private Long userId;
}
