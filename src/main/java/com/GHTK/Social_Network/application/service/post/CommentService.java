package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.CommentPostInput;
import com.GHTK.Social_Network.application.port.input.post.ReactionCommentPostInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.post.CommentPostPort;
import com.GHTK.Social_Network.application.port.output.post.ImageCommentPostPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.application.port.output.post.RedisImageTemplatePort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.*;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.CommentMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ReactionCommentMapper;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.CommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.CommentResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentPostInput, ReactionCommentPostInput {
  private final PostPort postPort;
  private final AuthPort authPort;
  private final CommentPostPort commentPostPort;
  private final FriendShipPort friendShipPort;
  private final ImageCommentPostPort imageCommentPostPort;

  private final RedisImageTemplatePort redisImageTemplatePort;

  private final CommentMapper commentMapper;
  private final ReactionCommentMapper reactionCommentMapper;

  private void checkCommentValid(Post post, User u) {
    if (post == null) {
      throw new CustomException("Post not found", HttpStatus.NOT_FOUND);
    }

    if (friendShipPort.isBlock(u.getUserId(), post.getUserId()) || !authPort.getUserById(post.getUserId()).getIsProfilePublic() && !u.getUserId().equals(authPort.getUserAuth().getUserId())) {
      throw new CustomException("You are not allowed to create a comment", HttpStatus.FORBIDDEN);
    }
  }

  @Override
  public CommentResponse createCommentSrc(CommentRequest comment) {
    Post post = postPort.findPostByPostId(comment.getPostId());
    User user = authPort.getUserAuth();
    checkCommentValid(post, user);

    Comment newComment = new Comment(
            new Date(),
            comment.getContent(),
            user.getUserId(),
            post.getUserId()
    );
    Comment saveComment = commentPostPort.saveComment(newComment);

    ImageComment imageComment = saveImageCommentRedis(comment.getPublicId(), authPort.getUserAuth(), newComment);

    CommentResponse response = commentMapper.commentToCommentResponse(saveComment);
    if (imageComment != null) {
      response.setImage(imageComment.getImageUrl());
    }
    return response;
  }


  @Override
  public CommentResponse createCommentChild(Long commentIdSrc, CommentRequest comment) {
    Post post = postPort.findPostByPostId(comment.getPostId());
    User user = authPort.getUserAuth();
    checkCommentValid(post, user);
    Comment parentComment = commentPostPort.findCommentById(commentIdSrc);
    if (parentComment == null) {
      throw new CustomException("Parent comment not found", HttpStatus.NOT_FOUND);
    }

    Comment newComment = new Comment(
            new Date(),
            comment.getContent(),
            user.getUserId(),
            post.getPostId()
    );

    ImageComment imageComment = saveImageCommentRedis(comment.getPublicId(), authPort.getUserAuth(), newComment);

    commentPostPort.setParentComment(commentIdSrc, newComment);

    newComment = commentPostPort.saveComment(newComment);


    CommentResponse response = commentMapper.commentToCommentResponse(newComment);
    if (imageComment != null) {
      response.setImage(imageComment.getImageUrl());
    }
    return response;
  }

  @Override
  public List<CommentResponse> getCommentsByPostId(Long postId) {
    Post post = postPort.findPostByPostId(postId);
    checkCommentValid(post, authPort.getUserAuth());

    return commentPostPort.findCommentByPostId(postId).stream().map(
            commentMapper::commentToCommentResponse
    ).toList();
  }

  @Override
  public CommentResponse getCommentById(Long commentId) {
    return null;
  }

  @Override
  public List<CommentResponse> getAllCommentChildById(Long id) {
    return List.of();
  }

  @Override
  public MessageResponse deleteComment(Long commentId) {
    Comment commentEntity = commentPostPort.findCommentById(commentId);
    if (commentEntity == null || !Objects.equals(commentEntity.getUserId(), authPort.getUserAuth().getUserId())) {
      throw new CustomException("Comment not found", HttpStatus.NOT_FOUND);
    }
    try {
      commentPostPort.deleteCommentById(commentId);
      return new MessageResponse("Comment deleted successfully");
    } catch (CustomException e) {
      throw new CustomException("Failed to delete comment", HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public CommentResponse updateComment(Long commentId, CommentRequest comment) {
    Post post = postPort.findPostByPostId(comment.getPostId());
    User user = authPort.getUserAuth();
    checkCommentValid(post, user);

    Comment updatedComment = commentPostPort.findCommentById(commentId);

    ImageComment imageComment = saveImageCommentRedis(comment.getPublicId(), authPort.getUserAuth(), updatedComment);

    imageCommentPostPort.deleteImageCommentById(comment.getImageDbId());

    updatedComment.setContent(comment.getContent());

    CommentResponse response = commentMapper.commentToCommentResponse(updatedComment);
    if (imageComment != null) {
      response.setImage(imageComment.getImageUrl());
    }
    return response;
  }

  private ImageComment saveImageCommentRedis(String publicId, User userSave, Comment commentSave) {
    ImageComment imageComment = null;
    if (Boolean.TRUE.equals(redisImageTemplatePort.existsByKey(publicId) && !publicId.isEmpty())) {
      imageComment = new ImageComment(
              userSave.getUserId(),
              commentSave.getCommentId(),
              redisImageTemplatePort.findByKey(publicId),
              new Date()
      );
      imageCommentPostPort.saveImageComment(imageComment);
    }
    return imageComment;
  }

  @Override
  public ReactionResponse handleReactionComment(Long commentId, String reactionType) {
    Post post = postPort.findPostByPostId(commentId);
    Comment updatedComment = commentPostPort.findCommentById(commentId);
    User userUpdateComment = authPort.getUserById(updatedComment.getUserId());
    checkCommentValid(post, userUpdateComment);

    EReactionType newReactionType;
    try {
      newReactionType = EReactionType.valueOf(reactionType.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new CustomException("Invalid reaction type", HttpStatus.BAD_REQUEST);
    }

    ReactionComment reactionComment = commentPostPort.findByCommentIdAndUserID(commentId, authPort.getUserAuth().getUserId());
    if (reactionComment == null) {
      ReactionComment newReactionComment = new ReactionComment(
              newReactionType,
              updatedComment.getCommentId(),
              authPort.getUserAuth().getUserId()
      );
      return reactionCommentMapper.commentToResponse(commentPostPort.saveReactionComment(newReactionComment));
    }

    reactionComment.setReactionType(newReactionType);
    return reactionCommentMapper.commentToResponse(commentPostPort.saveReactionComment(reactionComment));
  }

  @Override
  public List<ReactionResponse> getAllReactionInComment(Long commentId) {
    return List.of();
  }
}
