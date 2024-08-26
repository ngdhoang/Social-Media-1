package com.GHTK.Social_Network.infrastructure.payload.requests.relationship;


import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SetBlockRequest {
  @Min(value = 1, message = "userReceiveId must be greater than or equal to 1")
  private Long userId;
}