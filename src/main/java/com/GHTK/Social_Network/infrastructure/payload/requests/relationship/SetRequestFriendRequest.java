package com.GHTK.Social_Network.infrastructure.payload.requests.relationship;


import com.GHTK.Social_Network.common.customAnnotation.config.ValidPattern;
import com.GHTK.Social_Network.common.customAnnotation.logic.CustomPatternValidator;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SetRequestFriendRequest {
  @Min(value = 1, message = "userReceiveId must be greater than or equal to 1")
  private Long userReceiveId;

  @ValidPattern(value = CustomPatternValidator.UPDATE_FRIEND_STATUS)
  private String status;
}