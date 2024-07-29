package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionCountDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionUserDto;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Data
@Builder
public class ReactionCommentResponse {
    private String role;

    private Long roleId;

    private List<ReactionUserDto> users;

    private List<ReactionCountDto> reactions;

    @Builder
    public ReactionCommentResponse(String role, Long roleId, List<ReactionUserDto> users, List<ReactionCountDto> reactions) {
        this.role = role != null ? role : "comment";
        this.roleId = roleId;
        this.users = users;
        this.reactions = reactions;
    }
}
