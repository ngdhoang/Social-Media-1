package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionPostCountDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionPostUserDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReactionPostResponse {

  private Long postId;

  private List<ReactionPostUserDto> reactionUsers;

  private List<ReactionPostCountDto> reactionCount;
}
