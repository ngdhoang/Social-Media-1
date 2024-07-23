package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.model.Comment;
import com.GHTK.Social_Network.domain.model.ReactionComment;

import java.util.List;

public interface CommentPostPort {
  Comment saveComment(Comment comment);

  Comment findCommentById(Long id);

  List<Comment> findCommentByPostId(Long postId);

  void deleteCommentById(Long id);

  ReactionComment findByCommentIdAndUserID(Long commentId, Long userID);

  ReactionComment saveReactionComment(ReactionComment reactionComment);

  Comment setParentComment(Long commentParentId, Comment commentChild);
}
