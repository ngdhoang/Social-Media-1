package com.GHTK.Social_Network.infrastructure.payload.requests.post;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidPattern;
import com.GHTK.Social_Network.common.customAnnotation.logic.CustomPatternValidator;

import lombok.Data;

@Data
public class ReactionRequest {
    @ValidPattern(CustomPatternValidator.REACTION_TYPE)
    private String reactionType;
}
