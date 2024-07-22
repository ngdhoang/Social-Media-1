package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.CommentPostInput;
import com.GHTK.Social_Network.application.port.input.post.ReactionCommentPostInput;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.post.CommentPostPort;
import com.GHTK.Social_Network.application.port.output.post.ImageCommentPostPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EReactionType;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.Post;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.Comment;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.ImageComment;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.ReactionComment;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.CommentMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ReactionCommentMapper;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.CommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.CommentResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentPostInput, ReactionCommentPostInput {
  private final PostPort postPort;

  private final AuthPort authenticationRepositoryPort;

  private final CommentPostPort commentPostPort;

  private final FriendShipPort friendShipPort;

  private final RedisTemplate<String, String> imageRedisTemplate;

  private final ImageCommentPostPort imageCommentPostPort;


  private UserEntity getUserAuth() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username;

    if (principal instanceof UserDetails) {
      username = ((UserDetails) principal).getUsername();
    } else if (principal instanceof String) {
      username = (String) principal;
    } else {
      throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }

    return authenticationRepositoryPort.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid token"));
  }

  private void checkCommentValid(Post post, UserEntity u) {
    if (post == null) {
      throw new CustomException("Post not found", HttpStatus.NOT_FOUND);
    }

    if (friendShipPort.isBlock(u.getUserId(), post.getUserEntity().getUserId()) || !post.getUserEntity().getIsProfilePublic() && !u.getUserId().equals(getUserAuth().getUserId())) {
      throw new CustomException("You are not allowed to create a comment", HttpStatus.FORBIDDEN);
    }
  }

  @Override
  public CommentResponse createCommentSrc(CommentRequest comment) {
    Post post = postPort.findPostByPostId(comment.getPostId());
    UserEntity userEntity = getUserAuth();
    checkCommentValid(post, userEntity);

    Comment newComment = new Comment(
            new Date(),
            comment.getContent(),
            userEntity,
            post
    );
    Comment saveComment = commentPostPort.saveComment(newComment);

    ImageComment imageComment = saveImageCommentRedis(comment.getPublicId(), getUserAuth(), newComment);

    CommentResponse response = CommentMapper.INSTANCE.commentToCommentResponse(saveComment);
    if (imageComment != null) {
      response.setImage(imageComment.getImageUrl());
    }
    return response;
  }


  @Override
  public CommentResponse createCommentChild(Long commentIdSrc, CommentRequest comment) {
    Post post = postPort.findPostByPostId(comment.getPostId());
    UserEntity userEntity = getUserAuth();
    checkCommentValid(post, userEntity);
    Comment parentComment = commentPostPort.findCommentById(commentIdSrc);
    if (parentComment == null) {
      throw new CustomException("Parent comment not found", HttpStatus.NOT_FOUND);
    }

    Comment newComment = new Comment(
            new Date(),
            comment.getContent(),
            userEntity,
            post
    );

    ImageComment imageComment = saveImageCommentRedis(comment.getPublicId(), getUserAuth(), newComment);

    parentComment.addChildComment(newComment);

    newComment = commentPostPort.saveComment(newComment);

    CommentResponse response = CommentMapper.INSTANCE.commentToCommentResponse(newComment);
    if (imageComment != null) {
      response.setImage(imageComment.getImageUrl());
    }
    return response;
  }

  @Override
  public List<CommentResponse> getCommentsByPostId(Long postId) {
    Post post = postPort.findPostByPostId(postId);
    checkCommentValid(post, getUserAuth());

    return commentPostPort.findCommentByPostId(postId).stream().map(
            CommentMapper.INSTANCE::commentToCommentResponse
    ).toList();
  }

  @Override
  public MessageResponse deleteComment(Long commentId) {
    Comment comment = commentPostPort.findCommentById(commentId);
    if (comment == null || !Objects.equals(comment.getUserEntity().getUserId(), getUserAuth().getUserId())) {
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
    UserEntity userEntity = getUserAuth();
    checkCommentValid(post, userEntity);

    Comment updatedComment = commentPostPort.findCommentById(commentId);

    ImageComment imageComment = saveImageCommentRedis(comment.getPublicId(), getUserAuth(), updatedComment);

    imageCommentPostPort.deleteImageCommentById(comment.getImageDbId());

    updatedComment.setContent(comment.getContent());

    CommentResponse response = CommentMapper.INSTANCE.commentToCommentResponse(updatedComment);
    if (imageComment != null) {
      response.setImage(imageComment.getImageUrl());
    }
    return response;
  }

  private ImageComment saveImageCommentRedis(String publicId, UserEntity userEntitySave, Comment commentSave) {
    ImageComment imageComment = null;
    if (Boolean.TRUE.equals(imageRedisTemplate.hasKey(publicId)) && !publicId.isEmpty()) {
      imageComment = new ImageComment(
              userEntitySave,
              commentSave,
              imageRedisTemplate.opsForValue().get(publicId),
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
    checkCommentValid(post, updatedComment.getUserEntity());

    EReactionType newReactionType;
    try {
      newReactionType = EReactionType.valueOf(reactionType.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new CustomException("Invalid reaction type", HttpStatus.BAD_REQUEST);
    }

    ReactionComment reactionComment = commentPostPort.findByCommentIdAndUserID(commentId, getUserAuth().getUserId());
    if (reactionComment == null) {
      ReactionComment newReactionComment = new ReactionComment(
              newReactionType,
              updatedComment,
              getUserAuth()
      );
      return ReactionCommentMapper.INSTANCE.toReactionResponse(commentPostPort.saveReactionComment(newReactionComment));
    }

    reactionComment.setReactionType(newReactionType);
    return ReactionCommentMapper.INSTANCE.toReactionResponse(commentPostPort.saveReactionComment(reactionComment));
  }

  @Override
  public List<ReactionResponse> getAllReactionInComment(Long commentId) {
    return List.of();
  }
}
