package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidEReactionPostType;
import com.GHTK.Social_Network.domain.model.EReactionType;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class GetReactionPostRequest extends PaginationRequest {

  @ValidEReactionPostType
  private EReactionType reactionType;

  @Min(value = 1, message = "postId must be greater than or equal to 1")
  private Long postId;
}
