package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.CommentPostInput;
import com.GHTK.Social_Network.application.port.input.post.ReactionCommentPostInput;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.post.CommentPostPort;
import com.GHTK.Social_Network.application.port.output.post.ImageCommentPostPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EReactionTypeEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.ImageCommentEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.ReactionCommentEntity;
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

  private void checkCommentValid(PostEntity postEntity, UserEntity u) {
    if (postEntity == null) {
      throw new CustomException("Post not found", HttpStatus.NOT_FOUND);
    }

    if (friendShipPort.isBlock(u.getUserId(), postEntity.getUserEntity().getUserId()) || !postEntity.getUserEntity().getIsProfilePublic() && !u.getUserId().equals(getUserAuth().getUserId())) {
      throw new CustomException("You are not allowed to create a comment", HttpStatus.FORBIDDEN);
    }
  }

  @Override
  public CommentResponse createCommentSrc(CommentRequest comment) {
    PostEntity postEntity = postPort.findPostByPostId(comment.getPostId());
    UserEntity userEntity = getUserAuth();
    checkCommentValid(postEntity, userEntity);

    CommentEntity newCommentEntity = new CommentEntity(
            new Date(),
            comment.getContent(),
            userEntity,
            postEntity
    );
    CommentEntity saveCommentEntity = commentPostPort.saveComment(newCommentEntity);

    ImageCommentEntity imageCommentEntity = saveImageCommentRedis(comment.getPublicId(), getUserAuth(), newCommentEntity);

    CommentResponse response = CommentMapper.INSTANCE.commentToCommentResponse(saveCommentEntity);
    if (imageCommentEntity != null) {
      response.setImage(imageCommentEntity.getImageUrl());
    }
    return response;
  }


  @Override
  public CommentResponse createCommentChild(Long commentIdSrc, CommentRequest comment) {
    PostEntity postEntity = postPort.findPostByPostId(comment.getPostId());
    UserEntity userEntity = getUserAuth();
    checkCommentValid(postEntity, userEntity);
    CommentEntity parentCommentEntity = commentPostPort.findCommentById(commentIdSrc);
    if (parentCommentEntity == null) {
      throw new CustomException("Parent comment not found", HttpStatus.NOT_FOUND);
    }

    CommentEntity newCommentEntity = new CommentEntity(
            new Date(),
            comment.getContent(),
            userEntity,
            postEntity
    );

    ImageCommentEntity imageCommentEntity = saveImageCommentRedis(comment.getPublicId(), getUserAuth(), newCommentEntity);

    parentCommentEntity.addChildComment(newCommentEntity);

    newCommentEntity = commentPostPort.saveComment(newCommentEntity);

    CommentResponse response = CommentMapper.INSTANCE.commentToCommentResponse(newCommentEntity);
    if (imageCommentEntity != null) {
      response.setImage(imageCommentEntity.getImageUrl());
    }
    return response;
  }

  @Override
  public List<CommentResponse> getCommentsByPostId(Long postId) {
    PostEntity postEntity = postPort.findPostByPostId(postId);
    checkCommentValid(postEntity, getUserAuth());

    return commentPostPort.findCommentByPostId(postId).stream().map(
            CommentMapper.INSTANCE::commentToCommentResponse
    ).toList();
  }

  @Override
  public MessageResponse deleteComment(Long commentId) {
    CommentEntity commentEntity = commentPostPort.findCommentById(commentId);
    if (commentEntity == null || !Objects.equals(commentEntity.getUserEntity().getUserId(), getUserAuth().getUserId())) {
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
    PostEntity postEntity = postPort.findPostByPostId(comment.getPostId());
    UserEntity userEntity = getUserAuth();
    checkCommentValid(postEntity, userEntity);

    CommentEntity updatedCommentEntity = commentPostPort.findCommentById(commentId);

    ImageCommentEntity imageCommentEntity = saveImageCommentRedis(comment.getPublicId(), getUserAuth(), updatedCommentEntity);

    imageCommentPostPort.deleteImageCommentById(comment.getImageDbId());

    updatedCommentEntity.setContent(comment.getContent());

    CommentResponse response = CommentMapper.INSTANCE.commentToCommentResponse(updatedCommentEntity);
    if (imageCommentEntity != null) {
      response.setImage(imageCommentEntity.getImageUrl());
    }
    return response;
  }

  private ImageCommentEntity saveImageCommentRedis(String publicId, UserEntity userEntitySave, CommentEntity commentEntitySave) {
    ImageCommentEntity imageCommentEntity = null;
    if (Boolean.TRUE.equals(imageRedisTemplate.hasKey(publicId)) && !publicId.isEmpty()) {
      imageCommentEntity = new ImageCommentEntity(
              userEntitySave,
              commentEntitySave,
              imageRedisTemplate.opsForValue().get(publicId),
              new Date()
      );
      imageCommentPostPort.saveImageComment(imageCommentEntity);
    }
    return imageCommentEntity;
  }

  @Override
  public ReactionResponse handleReactionComment(Long commentId, String reactionType) {
    PostEntity postEntity = postPort.findPostByPostId(commentId);
    CommentEntity updatedCommentEntity = commentPostPort.findCommentById(commentId);
    checkCommentValid(postEntity, updatedCommentEntity.getUserEntity());

    EReactionTypeEntity newReactionType;
    try {
      newReactionType = EReactionTypeEntity.valueOf(reactionType.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new CustomException("Invalid reaction type", HttpStatus.BAD_REQUEST);
    }

    ReactionCommentEntity reactionCommentEntity = commentPostPort.findByCommentIdAndUserID(commentId, getUserAuth().getUserId());
    if (reactionCommentEntity == null) {
      ReactionCommentEntity newReactionCommentEntity = new ReactionCommentEntity(
              newReactionType,
              updatedCommentEntity,
              getUserAuth()
      );
      return ReactionCommentMapper.INSTANCE.toReactionResponse(commentPostPort.saveReactionComment(newReactionCommentEntity));
    }

    reactionCommentEntity.setReactionType(newReactionType);
    return ReactionCommentMapper.INSTANCE.toReactionResponse(commentPostPort.saveReactionComment(reactionCommentEntity));
  }

  @Override
  public List<ReactionResponse> getAllReactionInComment(Long commentId) {
    return List.of();
  }
}
