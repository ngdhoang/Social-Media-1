package com.GHTK.Social_Network.infrastructure.payload.requests.post;

import com.GHTK.Social_Network.domain.entity.post.EReactionType;
import lombok.Data;

import java.util.List;

@Data
public class ReactionPostRequest {
   private Long reactionId;

   private Long postId;

   private EReactionType reactionType;
}
