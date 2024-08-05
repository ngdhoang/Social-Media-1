package com.GHTK.Social_Network.infrastructure.payload.requests.relationship;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UnFriendShipRequest {
    @NotNull(message = "friendId cannot blank")
    @Min(value = 1, message = "friendId must be greater than or equal to 1")
    private Long userId;
}
