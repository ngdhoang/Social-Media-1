package com.GHTK.Social_Network.infrastructure.payload.requests.relationship;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidPattern;
import com.GHTK.Social_Network.common.customAnnotation.logic.ValidationPattern;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcceptFriendRequest {

    @NotNull(message = "friendId cannot blank")
    @Min(value = 1, message = "friendId must be greater than or equal to 1")
    private Long friendId;

    @NotNull(message = "isAccept cannot blank")
    @ValidPattern(value = ValidationPattern.BINARY)
    private int isAccept;
}
