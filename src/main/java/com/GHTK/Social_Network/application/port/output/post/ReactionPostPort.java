package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.domain.model.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetPostRequest;
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

  List<Map<EReactionType, Set<ReactionPost>>> getReactionGroupByPostId(Long postId);

  List<ReactionPost> getListReactionByPostId(Long postId, GetReactionPostRequest getReactionPostRequest);

  List<ReactionPost> getListReactionByPostIdAndListBlock(Long postId, GetReactionPostRequest getReactionPostRequest, List<Long> listBlock);

  List<Object[]> getListReactionInteractions(Long userId, GetPostRequest getPostRequest);
}