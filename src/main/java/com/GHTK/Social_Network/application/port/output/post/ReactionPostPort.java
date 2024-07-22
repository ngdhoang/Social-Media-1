package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ReactionPostEntity;

import java.util.List;

public interface ReactionPostPort {
  ReactionPostEntity findByPostIdAndUserID(Long postId, Long userId);

  ReactionPostEntity saveReaction(ReactionPostEntity reactionPostEntity);

  void deleteReaction(ReactionPostEntity reactionPostEntity);

  List<ReactionPostEntity> findByPostId(Long postId);
}
