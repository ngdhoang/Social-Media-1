package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidPattern;
import com.GHTK.Social_Network.common.customAnnotation.logic.CustomPatternValidator;

import lombok.Data;

@Data
public class ReactionPostRequest {
    @ValidPattern(value = CustomPatternValidator.REACTION_TYPE)
    private String reactionType;
}
