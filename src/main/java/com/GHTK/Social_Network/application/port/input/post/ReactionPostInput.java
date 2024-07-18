package com.GHTK.Social_Network.application.port.input.post;

import com.GHTK.Social_Network.infrastructure.payload.requests.post.ReactionPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionPostResponse;

import java.util.List;

public interface ReactionPostInput      {
    ReactionPostResponse createReactionPost(ReactionPostRequest reactionPostRequest);

    ReactionPostResponse updateReactionPost(ReactionPostRequest reactionPostRequest);

    MessageResponse deleteReactionPost(Long id);

    List<ReactionPostResponse> getAllReactionPostByPostId(Long postId);

}
