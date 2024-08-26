package com.GHTK.Social_Network.application.port.input.post;

import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionCommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ReactionRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionCommentResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;

public interface ReactionCommentInput {
  ReactionResponse handleReactionComment(Long commentId, ReactionRequest reactionPostRequest);

  ReactionCommentResponse getListReactionInComment(Long postId, GetReactionCommentRequest getReactionCommentRequest);
}
