package com.GHTK.Social_Network.infrastructure.payload.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SetRequestFriendRequest {
    @NotBlank(message = "userId cannot blank")
    private Long userReceiveId;

    @NotBlank(message = "status cannot blank")
    @NotNull(message = "status cannot blank")
    private Integer status;
}
