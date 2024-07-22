package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.ReactionCommentEntity;

import java.util.List;

public interface CommentPostPort {
  CommentEntity saveComment(CommentEntity commentEntity);

  CommentEntity findCommentById(Long id);

  List<CommentEntity> findCommentByPostId(Long postId);

  void deleteCommentById(Long id);

  ReactionCommentEntity findByCommentIdAndUserID(Long commentId, Long userID);

  ReactionCommentEntity saveReactionComment(ReactionCommentEntity reactionCommentEntity);
}
