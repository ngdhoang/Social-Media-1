package com.GHTK.Social_Network.infrastructure.payload.requests;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SetRequestFriendRequest {
  private Long userReceiveId;

  @NotNull(message = "status cannot blank")
  private Integer status;
}
