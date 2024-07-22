package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.post.CommentPostPort;
import com.GHTK.Social_Network.infrastructure.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentPostPortImpl implements CommentPostPort {
  private final CommentRepository commentRepository;

  @Override
  public CommentEntity saveComment(CommentEntity comment) {
    return commentRepository.save(comment);
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
}
