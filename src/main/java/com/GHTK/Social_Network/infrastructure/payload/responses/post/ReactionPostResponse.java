package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionCountDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionUserDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReactionPostResponse {
  private String role;

  private Long roleId;

  private List<ReactionUserDto> users;

  private List<ReactionCountDto> reactions;

  @Builder
    public ReactionPostResponse(String role, Long roleId, List<ReactionUserDto> users, List<ReactionCountDto> reactions) {
        this.role = role != null ? role : "post";
        this.roleId = roleId;
        this.users = users;
        this.reactions = reactions;
    }
}
