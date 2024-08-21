package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.domain.model.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.GetPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.GetReactionPostRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ReactionPostPort {
  ReactionPost findByPostIdAndUserID(Long postId, Long userId);

  ReactionPost saveReaction(ReactionPost reactionPost);

  void deleteReaction(ReactionPost reactionPost);

  List<ReactionPost> findByPostId(Long postId);

  List<Map<EReactionType, Set<ReactionPost>>> getReactionGroupByPostId(Long postId);

  List<ReactionPost> getListReactionByPostIdAndListBlock(Long postId, GetReactionPostRequest getReactionPostRequest, List<Long> listBlock);

  List<Object[]> getListReactionInteractions(Long userId, GetPostRequest getPostRequest);
}