package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.model.Comment;
import com.GHTK.Social_Network.domain.model.ReactionComment;

import java.awt.print.Pageable;
import java.util.List;

public interface CommentPostPort {
  Comment saveComment(Comment comment);

  Comment findCommentById(Long id);

  List<Comment> findCommentByPostId(Long postId);

  List<Comment> findCommentByParentId(Long commentId);

  List<Comment> findCommentParentByPostId(Long postId);

  void deleteCommentById(Long id);

  ReactionComment findByCommentIdAndUserID(Long commentId, Long userID);

  ReactionComment saveReactionComment(ReactionComment reactionComment);

  Comment setParentComment(Long commentParentId, Comment commentChild);
}
