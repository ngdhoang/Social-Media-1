package com.GHTK.Social_Network.infrastructure.payload.requests;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class GetReactionPostRequest extends PaginationRequest {

  private String reactionType;

  @Min(value = 1, message = "postId must be greater than or equal to 1")
  private Long postId;
}
