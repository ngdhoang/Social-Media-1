package com.GHTK.Social_Network.application.port.input.post;

import com.GHTK.Social_Network.infrastructure.payload.requests.GetPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ReactionRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ActivityInteractionResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionPostResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;

import java.util.List;

public interface ReactionPostInput {
  ReactionResponse handleReactionPost(Long postId, ReactionRequest reactionPostRequest);

  ReactionPostResponse getListReactionInPost(Long postId, GetReactionPostRequest getReactionPostRequest);

  List<ActivityInteractionResponse> getListReactionInteractions(GetPostRequest getPostRequest);
}