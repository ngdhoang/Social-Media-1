package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidPattern;
import com.GHTK.Social_Network.common.customAnnotation.logic.CustomPatternValidator;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class GetReactionPostRequest extends PaginationRequest {
  @ValidPattern(CustomPatternValidator.REACTION_TYPE)
  private String reactionType;

  @Min(value = 1, message = "postId must be greater than or equal to 1")
  private Long postId;
}
