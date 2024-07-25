package com.GHTK.Social_Network.infrastructure.payload.requests.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {
  @NotNull(message = "postId cannot blank")
  private Long postId;

  @NotBlank(message = "content cannot blank")
  private String content;

  private String publicId;

  private Long imageUrl;
}
