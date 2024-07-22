package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.post.CommentPostPort;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.Comment;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.ReactionComment;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.CommentRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ReactionCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentPostPortImpl implements CommentPostPort {
  private final CommentRepository commentRepository;
  private final ReactionCommentRepository reactionCommentRepository;

  @Override
  public Comment saveComment(Comment comment) {
    return commentRepository.save(comment);
  }

  @Override
  public Comment findCommentById(Long id) {
    return commentRepository.findById(id).orElse(null);
  }

  @Override
  public List<Comment> findCommentByPostId(Long postId) {
    return commentRepository.findAllByPostId(postId);
  }

  @Override
  public void deleteCommentById(Long id) {
    commentRepository.deleteById(id);
  }

  @Override
  public ReactionComment findByCommentIdAndUserID(Long commentId, Long userID) {
    return reactionCommentRepository.findByCommentIdAndUserId(userID, commentId);
  }

  @Override
  public ReactionComment saveReactionComment(ReactionComment reactionComment) {
    return reactionCommentRepository.save(reactionComment);
  }
}
