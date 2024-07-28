package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.model.post.comment.Comment;
import com.GHTK.Social_Network.domain.model.post.ReactionPost;

import java.util.List;

public interface CommentPostPort {
  Comment saveComment(Comment comment);

  Comment findCommentById(Long id);

  List<Comment> findCommentByPostId(Long postId);

  List<Comment> findCommentByParentId(Long commentId);

  List<Comment> findCommentParentByPostId(Long postId);

  List<Comment> findCommentsByInteractions(Long userId);

  void deleteCommentById(Long id);

  ReactionPost findByCommentIdAndUserID(Long commentId, Long userID);

  Comment setParentComment(Long commentParentId, Comment commentChild);
}
