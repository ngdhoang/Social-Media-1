package com.GHTK.Social_Network.infrastructure.payload.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UnFriendShipRequest {
  @NotNull(message = "friendId cannot blank")
  private Long friendId;
}
