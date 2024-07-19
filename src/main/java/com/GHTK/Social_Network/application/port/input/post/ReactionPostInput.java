package com.GHTK.Social_Network.application.port.input.post;

import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionPostResponse;

import java.util.List;

public interface ReactionPostInput {
  ReactionPostResponse handleReactionPost(Long postId, String reactionType);

  List<ReactionPostResponse> getAllReactionInPost(Long postId);
}
