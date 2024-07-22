package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.CommentPostInput;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.post.CommentPostPort;
import com.GHTK.Social_Network.application.port.output.post.ImageCommentPostPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.infrastructure.MapperEntity.UserMapper;
import com.GHTK.Social_Network.infrastructure.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.entity.post.comment.ImageCommentEntity;
import com.GHTK.Social_Network.infrastructure.entity.user.UserEntity;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.CommentMapper;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.CommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.CommentResponse;
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
public class CommentService implements CommentPostInput {
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

    return UserMapper.INSTANCE.toEntity(authenticationRepositoryPort.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid token")));
  }

  private void checkCommentValid(PostEntity postEntity, UserEntity userEntity) {
    if (postEntity == null) {
      throw new CustomException("Post not found", HttpStatus.NOT_FOUND);
    }

    if (friendShipPort.isBlock(userEntity.getUserId(), postEntity.getUserEntity().getUserId()) || !postEntity.getUserEntity().getIsProfilePublic()) {
      throw new CustomException("You are not allowed to create a comment", HttpStatus.FORBIDDEN);

    }
  }

  @Override
  public CommentResponse createCommentSrc(CommentRequest comment) {
    PostEntity postEntity = postPort.findPostByPostId(comment.getPostId());
    UserEntity userEntity = getUserAuth();
    checkCommentValid(postEntity, userEntity);

    CommentEntity newComment = new CommentEntity(
            new Date(),
            comment.getContent(),
            userEntity,
            postEntity
    );
    CommentEntity saveComment = commentPostPort.saveComment(newComment);

    ImageCommentEntity imageComment = saveImageCommentRedis(comment.getPublicId(), getUserAuth(), newComment);

    CommentResponse response = CommentMapper.INSTANCE.commentToCommentResponse(saveComment);
    if (imageComment != null) {
      response.setImage(imageComment.getImageUrl());
    }
    return response;
  }


  @Override
  public CommentResponse createCommentChild(Long commentIdSrc, CommentRequest comment) {
    PostEntity postEntity = postPort.findPostByPostId(comment.getPostId());
    UserEntity userEntity = getUserAuth();
    checkCommentValid(postEntity, userEntity);
    CommentEntity parentComment = commentPostPort.findCommentById(commentIdSrc);
    if (parentComment == null) {
      throw new CustomException("Parent comment not found", HttpStatus.NOT_FOUND);
    }

    CommentEntity newComment = new CommentEntity(
            new Date(),
            comment.getContent(),
            userEntity,
            postEntity
    );

    ImageCommentEntity imageComment = saveImageCommentRedis(comment.getPublicId(), getUserAuth(), newComment);

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
    PostEntity postEntity = postPort.findPostByPostId(postId);
    checkCommentValid(postEntity, getUserAuth());

    return commentPostPort.findCommentByPostId(postId).stream().map(
            CommentMapper.INSTANCE::commentToCommentResponse
    ).toList();
  }

  @Override
  public MessageResponse deleteComment(Long commentId) {
    CommentEntity comment = commentPostPort.findCommentById(commentId);
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
    PostEntity postEntity = postPort.findPostByPostId(comment.getPostId());
    UserEntity userEntity = getUserAuth();
    checkCommentValid(postEntity, userEntity);

    CommentEntity updatedComment = commentPostPort.findCommentById(commentId);

    ImageCommentEntity imageComment = saveImageCommentRedis(comment.getPublicId(), getUserAuth(), updatedComment);

    imageCommentPostPort.deleteImageCommentById(comment.getImageDbId());

    updatedComment.setContent(comment.getContent());

    CommentResponse response = CommentMapper.INSTANCE.commentToCommentResponse(updatedComment);
    if (imageComment != null) {
      response.setImage(imageComment.getImageUrl());
    }
    return response;
  }

  private ImageCommentEntity saveImageCommentRedis(String publicId, UserEntity userEntitySave, CommentEntity commentSave) {
    ImageCommentEntity imageComment = null;
    if (Boolean.TRUE.equals(imageRedisTemplate.hasKey(publicId)) && !publicId.isEmpty()) {
      imageComment = new ImageCommentEntity(
              userEntitySave,
              commentSave,
              imageRedisTemplate.opsForValue().get(publicId),
              new Date()
      );
      imageCommentPostPort.saveImageComment(imageComment);
    }
    return imageComment;
  }
}
