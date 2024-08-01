package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.domain.model.post.ReactionComment;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionCommentRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ReactionCommentPort {
    ReactionComment findByCommentIdAndUserID(Long postId, Long userId);

    ReactionComment saveReaction(ReactionComment reactionComment);

    void deleteReaction(ReactionComment reactionComment);

    List<ReactionComment> findByCommentId(Long postId, List<Long> userIds);

    int countReactionByCommentId(Long postId);

    int countReactionByCommentIdAndType(Long postId, EReactionType reactionType);

    List<Map<EReactionType, Set<ReactionComment>>> getReactionGroupByCommentId(Long postId);


    List<ReactionComment> getListReactionByCommentId(Long postId, GetReactionCommentRequest getReactionCommentRequest);


    List<ReactionComment> getListReactionByCommentIdAndListBlock(Long commentId, GetReactionCommentRequest getReactionCommentRequest, List<Long> listBlock);
}