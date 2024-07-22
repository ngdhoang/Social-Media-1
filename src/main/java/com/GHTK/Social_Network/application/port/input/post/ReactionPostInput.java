package com.GHTK.Social_Network.application.port.input.post;

import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;

import java.util.List;

public interface ReactionPostInput {
  ReactionResponse handleReactionPost(Long postId, String reactionType);

  List<ReactionResponse> getAllReactionInPost(Long postId);
}
