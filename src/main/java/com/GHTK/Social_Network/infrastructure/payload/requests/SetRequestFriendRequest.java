package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.domain.entity.EFriendshipStatus;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SetRequestFriendRequest {
    @NotBlank(message = "userId cannot blank")
    private Long userReceiveId;

    @NotBlank(message = "status cannot blank")
    @Enumerated
    private EFriendshipStatus status;
}
