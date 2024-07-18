package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.entity.post.Post;
import com.GHTK.Social_Network.domain.entity.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;

import java.util.List;

public interface ReactionPostPort {
    ReactionPost saveReactionPost(ReactionPost reactionPost);

    Boolean deleteReactionPostById(Long id);

    List<ReactionPost> findAllReactionPostByPost(Post post);

    ReactionPost findReactionPostByIdAndPost(Long id,Post post);

    ReactionPost findReactionPostById(Long id);
}
