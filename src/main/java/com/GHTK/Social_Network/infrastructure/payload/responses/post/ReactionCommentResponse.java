package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionCountDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionUserDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReactionCommentResponse {
    private String role = "comment";

    private Long roleId;

    private List<ReactionUserDto> users;

    private List<ReactionCountDto> reactions;
}
