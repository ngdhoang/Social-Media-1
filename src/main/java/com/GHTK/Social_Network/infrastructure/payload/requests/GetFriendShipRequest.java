package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.application.customAnnotation.config.ValidEFriendShipStatus;
import com.GHTK.Social_Network.domain.entity.EFriendshipStatus;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class GetFriendShipRequest extends PaginationRequest{
    @ValidEFriendShipStatus
    private EFriendshipStatus status;

    @Nullable
    @Min(value = 1, message = "userId must be greater than or equal to 1")
    private Long userId;

}
