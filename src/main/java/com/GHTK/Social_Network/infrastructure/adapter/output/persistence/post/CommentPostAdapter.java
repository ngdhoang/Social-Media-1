package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.post;

import com.GHTK.Social_Network.application.port.output.post.CommentPostPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.post.comment.Comment;
import com.GHTK.Social_Network.domain.model.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.CommentRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ReactionPostRepository;
import com.GHTK.Social_Network.infrastructure.mapper.CommentMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.ReactionCommentMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentPostAdapter implements CommentPostPort {
  private final CommentRepository commentRepository;
  private final ReactionPostRepository reactionPostRepository;

  private final CommentMapperETD commentMapperETD;
  private final ReactionCommentMapperETD reactionCommentMapperETD;

  @Override
  public Comment saveComment(Comment comment) {
    return commentMapperETD.toDomain(
            commentRepository.save(commentMapperETD.toEntity(comment)
            )
    );
  }

  @Override
  public Comment findCommentById(Long id) {
    return commentMapperETD.toDomain(commentRepository.findById(id).orElse(null));
  }

  @Override
  public List<Comment> findCommentByPostId(Long postId) {
    return commentRepository.findAllByPostId(postId).stream().map(
            commentMapperETD::toDomain
    ).toList();
  }

  @Override
  public List<Comment> findCommentByParentId(Long commentId) {
    return commentRepository.findAllByCommentParentId(commentId).stream()
            .map(commentMapperETD::toDomain)
            .toList();
  }

  @Override
  public List<Comment> findCommentParentByPostId(Long postId) {
    return commentRepository.findAllCommentParentIdByPostId(postId).stream()
            .map(commentMapperETD::toDomain)
            .toList();
  }

  @Override
  public List<Comment> findCommentsByInteractions(Long postId) {
    return List.of();
  }

  @Override

  public void deleteCommentById(Long id) {
    commentRepository.deleteById(id);
  }

  @Override
  public ReactionPost findByCommentIdAndUserID(Long commentId, Long userID) {
    return reactionCommentMapperETD.toDomain(reactionPostRepository.findByCommentIdAndUserId(userID, commentId));
  }

  @Override
  public Comment setParentComment(Long commentParentId, Comment commentChild) {
    CommentEntity commentEntity = commentRepository.findById(commentParentId).orElse(null);
    if (commentEntity == null) {
      throw new CustomException("Comment not found", HttpStatus.NOT_FOUND);
    }

    commentChild.setParentCommentId(commentEntity.getCommentId());
    return saveComment(commentChild);
  }

}
