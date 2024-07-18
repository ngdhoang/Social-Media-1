package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import com.GHTK.Social_Network.domain.entity.post.EReactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReactionPostResponse {
    private Long reactionPostId;

    private Long userId;

    private EReactionType reactionType;

    private String firstName;

    private String lastName;

    private String avatar;

    private Boolean friendCheck;
}
