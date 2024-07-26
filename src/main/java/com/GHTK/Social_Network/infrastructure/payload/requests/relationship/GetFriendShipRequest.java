package com.GHTK.Social_Network.infrastructure.payload.requests.relationship;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidPattern;
import com.GHTK.Social_Network.common.customAnnotation.logic.CustomPatternValidator;
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetFriendShipRequest extends PaginationRequest {
    @ValidPattern(value = CustomPatternValidator.FILTER_FRIEND_STATUS)
    private String status;

    @Min(value = 1, message = "userId must be greater than or equal to 1")
    private Long userId;

}
