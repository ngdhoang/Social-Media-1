package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.model.post.comment.Comment;
import com.GHTK.Social_Network.domain.model.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetCommentRequest;

import java.util.List;

public interface CommentPostPort {
  Comment saveComment(Comment comment);

  Comment findCommentById(Long id);

  List<Comment> getListCommentByPostId(Long postId, List<Long> blockIds, GetCommentRequest getCommentRequest);

  List<Comment> getListCommentByParentId(Long commentId, List<Long> blockIds, GetCommentRequest getCommentRequest);

  List<Comment> findCommentParentByPostId(Long postId);

  List<Comment> findCommentsByInteractions(Long userId);

  void deleteCommentById(Long id);

//  ReactionPost findByCommentIdAndUserID(Long commentId, Long userID);

  Comment setParentComment(Long commentParentId, Comment commentChild);

  void increaseCommentCount(Long commentId);

  void decreaseCommentCount(Long commentId, Long quantity);

  void increaseReactionCount(Long commentId);

  void decreaseReactionCount(Long commentId, Long quantity);
}
