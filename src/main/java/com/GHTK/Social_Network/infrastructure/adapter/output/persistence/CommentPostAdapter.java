package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.post.CommentPostPort;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.ReactionCommentEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.CommentRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ReactionCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentPostAdapter implements CommentPostPort {
  private final CommentRepository commentRepository;
  private final ReactionCommentRepository reactionCommentRepository;

  @Override
  public CommentEntity saveComment(CommentEntity commentEntity) {
    return commentRepository.save(commentEntity);
  }

  @Override
  public CommentEntity findCommentById(Long id) {
    return commentRepository.findById(id).orElse(null);
  }

  @Override
  public List<CommentEntity> findCommentByPostId(Long postId) {
    return commentRepository.findAllByPostId(postId);
  }

  @Override
  public void deleteCommentById(Long id) {
    commentRepository.deleteById(id);
  }

  @Override
  public ReactionCommentEntity findByCommentIdAndUserID(Long commentId, Long userID) {
    return reactionCommentRepository.findByCommentIdAndUserId(userID, commentId);
  }

  @Override
  public ReactionCommentEntity saveReactionComment(ReactionCommentEntity reactionCommentEntity) {
    return reactionCommentRepository.save(reactionCommentEntity);
  }
}
