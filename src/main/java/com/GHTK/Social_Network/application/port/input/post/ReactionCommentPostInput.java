package com.GHTK.Social_Network.application.port.input.post;

import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;

import java.util.List;

public interface ReactionCommentPostInput {
  ReactionResponse handleReactionComment(Long commentId, String reactionType);

  List<ReactionResponse> getAllReactionInComment(Long commentId);
}
