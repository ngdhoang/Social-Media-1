package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ReactionPost;

import java.util.List;

public interface ReactionPostPort {
  ReactionPost  findByPostIdAndUserID(Long postId, Long userId);

  ReactionPost saveReaction(ReactionPost reactionPost);

  void deleteReaction(ReactionPost reactionPost);

  List<ReactionPost> findByPostId(Long postId);
}
