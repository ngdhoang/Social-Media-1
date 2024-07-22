package com.GHTK.Social_Network.domain.model.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagUser {
  private Long tagUserId;

  private Long postId;

  private Long userId;
}
