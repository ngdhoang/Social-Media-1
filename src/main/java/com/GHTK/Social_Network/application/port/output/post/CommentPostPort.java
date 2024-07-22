package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.infrastructure.entity.post.comment.CommentEntity;

import java.util.List;

public interface CommentPostPort {
  CommentEntity saveComment(CommentEntity comment);

  CommentEntity findCommentById(Long id);

  List<CommentEntity> findCommentByPostId(Long postId);

  void deleteCommentById(Long id);
}
