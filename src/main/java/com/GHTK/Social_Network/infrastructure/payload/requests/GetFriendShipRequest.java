package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidEFriendShipStatus;
import com.GHTK.Social_Network.domain.model.EFriendshipStatus;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetFriendShipRequest extends PaginationRequest{
//    @ValidEFriendShipStatus
    private EFriendshipStatus status;

    @Min(value = 1, message = "userId must be greater than or equal to 1")
    private Long userId;

}
