package com.GHTK.Social_Network.infrastructure.payload.responses;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.PostBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ActivityInteractionResponse {
    private UserBasicDto owner;

    private Long roleId;

    private PostBasicDto post;

    private String role;

    private Instant createAt;

    private String content;

    private Long parentCommentId;

    private String imageUrl;

    private EReactionType reactionType;

}
