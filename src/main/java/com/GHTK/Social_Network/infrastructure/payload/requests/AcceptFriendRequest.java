package com.GHTK.Social_Network.infrastructure.payload.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcceptFriendRequest {
  @NotNull(message = "friendId cannot blank")
  private Long friendId;

  @NotNull(message = "isAccept cannot blank")
  private int isAccept;
}
