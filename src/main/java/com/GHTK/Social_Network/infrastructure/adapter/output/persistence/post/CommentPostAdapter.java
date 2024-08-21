package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.post;

import com.GHTK.Social_Network.application.port.output.post.CommentPostPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.post.comment.Comment;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.CommentNode;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.CommentRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ReactionPostRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.node.CommentNodeRepository;
import com.GHTK.Social_Network.infrastructure.mapper.CommentMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.ReactionCommentMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentPostAdapter implements CommentPostPort {
  private final CommentRepository commentRepository;
  private final CommentNodeRepository commentNodeRepository;

  private final CommentMapperETD commentMapperETD;

  @Override
  public Comment saveComment(Comment comment) {
    Comment newComment = commentMapperETD.toDomain(
            commentRepository.save(commentMapperETD.toEntity(comment)
            )
    );

    return newComment;
  }

  @Override
  public Comment findCommentById(Long id) {
    return commentMapperETD.toDomain(commentRepository.findById(id).orElse(null));
  }

  @Override
  public List<Comment> getListCommentByPostId(Long postId, List<Long> blockIds, GetCommentRequest getCommentRequest) {
    Pageable pageable = getCommentRequest.toPageable();
    return commentRepository.getListParentIdByPostIdAndListBlock(postId, blockIds, pageable).stream().map(
            commentMapperETD::toDomain
    ).toList();
  }

  @Override
  public List<Comment> getListCommentByParentId(Long commentId, List<Long> blockIds, GetCommentRequest getCommentRequest) {
    Pageable pageable = getCommentRequest.toPageable();

    return commentRepository.getListByCommentParentIdAndListBlock(commentId, blockIds, pageable).stream()
            .map(commentMapperETD::toDomain)
            .toList();
  }

  @Override
  public List<Comment> getListCommentByUserId(Long userId, GetCommentRequest getCommentRequest) {
    Pageable pageable = getCommentRequest.toPageable();
    return commentRepository.getListCommentByUserId(userId, pageable).stream()
            .map(commentMapperETD::toDomain)
            .toList();
  }

  @Override

  public void deleteCommentById(Long id) {
    commentRepository.deleteById(id);
    commentNodeRepository.deleteCommentNodeByCommentId(id);
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

  @Override
  public void increaseCommentCount(Long commentId) {
    commentRepository.increaseCommentCount(commentId);
  }


  @Override
  public void decreaseCommentCount(Long commentId, Long quantity) {
    commentRepository.decreaseCommentCount(commentId, quantity);
  }


  @Override
  public void increaseReactionCount(Long commentId) {
    commentRepository.increaseReactionCount(commentId);
  }

  @Override
  public void decreaseReactionCount(Long commentId, Long quantity) {
    commentRepository.decreaseReactionCount(commentId, quantity);
  }

}
