package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.model.EReactionType;
import com.GHTK.Social_Network.domain.model.ReactionPost;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionPostRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ReactionPostPort {
  ReactionPost findByPostIdAndUserID(Long postId, Long userId);

  ReactionPost saveReaction(ReactionPost reactionPost);

  void deleteReaction(ReactionPost reactionPost);

  List<ReactionPost> findByPostId(Long postId);

  int countReactionByPostId(Long postId);

  int countReactionByPostIdAndType(Long postId, EReactionType reactionType);

  Map<EReactionType, Set<ReactionPost>> getReactionGroupByPostId(Long postId);

  List<ReactionPost> getByPostIdAndType(Long postId, GetReactionPostRequest getReactionPostRequest);

  List<ReactionPost> getListReactionByPostId(Long postId, GetReactionPostRequest getReactionPostRequest);
}
