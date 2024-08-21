package com.GHTK.Social_Network.domain.model.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
  private Long postId;

  private String content;

  private Instant createAt;

  private Instant updateAt;

  private Long reactionsQuantity = 0L;

  private Long commentQuantity = 0L;

  private EPostStatus postStatus;

  private Long userId;
}
