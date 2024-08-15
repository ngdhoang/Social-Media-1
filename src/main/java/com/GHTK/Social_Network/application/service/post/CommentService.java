package com.GHTK.Social_Network.application.service.post;

import ai.djl.translate.TranslateException;
import ai.onnxruntime.OrtException;
import com.GHTK.Social_Network.application.port.input.post.CommentPostInput;
import com.GHTK.Social_Network.application.port.input.post.ImagePostInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.PhoBERTPortInput;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.post.CommentPostPort;
import com.GHTK.Social_Network.application.port.output.post.ImagePostPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.application.port.output.post.RedisImageTemplatePort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.event.comment.CommentCreateEvent;
import com.GHTK.Social_Network.domain.model.post.EPostStatus;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.comment.Comment;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.CommentMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.PostMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.UserMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetCommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.CommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ActivityInteractionResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.InteractionResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentPostInput {
  private final PostPort postPort;
  private final AuthPort authPort;
  private final CommentPostPort commentPostPort;
  private final FriendShipPort friendShipPort;
  private final RedisImageTemplatePort redisImageTemplatePort;
  private final ImagePostPort imagePostPort;

  private final PhoBERTPortInput phoBERTPortInput;

  private final CommentMapper commentMapper;
  private final UserMapper userMapper;

  private final PostMapper postMapper;

  private final ApplicationEventPublisher applicationEventPublisher;

  private User getUserAuth() {
    User user = authPort.getUserAuth();
    return user == null ? User.builder().userId(0L).build() : user;
  }

  private void checkCommentValid(Post post, User u) {
    if (post == null) {
      throw new CustomException("Post not found", HttpStatus.NOT_FOUND);
    }

    Long postOwnerId = post.getUserId();

    if (u.getUserId().equals(postOwnerId)) {
      return;
    }

    if (friendShipPort.isBlock(u.getUserId(), postOwnerId)
            || (!authPort.getUserById(postOwnerId).getIsProfilePublic())
            || post.getPostStatus().equals(EPostStatus.PRIVATE)
            || (post.getPostStatus().equals(EPostStatus.FRIEND) && (u.getUserId().equals(0) || !friendShipPort.isFriend(u.getUserId(), postOwnerId)))
    ) {
      throw new CustomException("Not allow", HttpStatus.FORBIDDEN);
    }
  }

  @Override
  public CommentResponse createCommentRoot(CommentRequest comment) throws TranslateException, OrtException {
    Post post = postPort.findPostByPostId(comment.getPostId());
    User user = getUserAuth();
    checkCommentValid(post, user);

    boolean isToxic = phoBERTPortInput.isToxic(comment.getContent());

    if (isToxic) {
      throw new CustomException("Comment is toxic", HttpStatus.BAD_REQUEST);
    }

    String imageCommentUrl = getImageUrlCommentInRedis(comment.getPublicId(), getUserAuth());
    Comment newComment = new Comment(
            comment.getContent(),
            user.getUserId(),
            post.getPostId(),
            imageCommentUrl
    );
    Comment saveComment = commentPostPort.saveComment(newComment);
    postPort.incrementCommentQuantity(post.getPostId());

    applicationEventPublisher.publishEvent(new CommentCreateEvent(saveComment));

    UserBasicDto userBasicDto = userMapper.userToUserBasicDto(user);

    return commentMapper.commentToCommentResponse(saveComment, userBasicDto);
  }


  @Override
  public CommentResponse createCommentChild(Long commentIdSrc, CommentRequest comment) throws TranslateException, OrtException {
    Post post = postPort.findPostByPostId(comment.getPostId());
    User user = this.getUserAuth();
    checkCommentValid(post, user);

    boolean isToxic = phoBERTPortInput.isToxic(comment.getContent());

    if (isToxic) {
      throw new CustomException("Comment is toxic", HttpStatus.BAD_REQUEST);
    }

    Comment parentComment = commentPostPort.findCommentById(commentIdSrc);
    if (parentComment == null) {
      throw new CustomException("Parent comment not found", HttpStatus.NOT_FOUND);
    }


    if (friendShipPort.isBlock(user.getUserId(), parentComment.getUserId()) || friendShipPort.isBlock(parentComment.getUserId(), post.getUserId())) {
      throw new CustomException("Not allow comment", HttpStatus.FORBIDDEN);
    }

    String imageComment = getImageUrlCommentInRedis(comment.getPublicId(), this.getUserAuth());
    Comment newComment = new Comment(
            comment.getContent(),
            user.getUserId(),
            post.getPostId(),
            imageComment
    );
    commentPostPort.setParentComment(commentIdSrc, newComment);
    newComment = commentPostPort.saveComment(newComment);
    postPort.incrementCommentQuantity(post.getPostId());
    Long commentParentId = parentComment.getCommentId();
    commentPostPort.increaseCommentCount(commentParentId);

    applicationEventPublisher.publishEvent(new CommentCreateEvent(newComment));

    UserBasicDto userBasicDto = userMapper.userToUserBasicDto(user);

    return commentMapper.commentToCommentResponse(newComment, userBasicDto);
  }

  @Override
  public List<CommentResponse> getCommentsByPostId(Long postId, GetCommentRequest getCommentRequest) {
    Post post = postPort.findPostByPostId(postId);
    User user = authPort.getUserAuthOrDefaultVirtual();
    checkCommentValid(post, user);
    List<Comment> listComment = commentPostPort.getListCommentByPostId(postId, friendShipPort.getListBlockBoth(getUserAuth().getUserId()), getCommentRequest);
    return listComment.stream()
            .filter(comment -> comment.getParentCommentId() == null)
            .map(rootComment ->
                    processCommentWithChildren(rootComment, listComment)
            )
            .collect(Collectors.toList());
  }


  @Override
  public List<ActivityInteractionResponse> getListCommentInteractions(GetCommentRequest getCommentRequest) {
    User user = authPort.getUserAuthOrDefaultVirtual();
    if (user.getUserId().equals(0L)) {
      throw new CustomException("Not authorized", HttpStatus.UNAUTHORIZED);
    }
    List<Comment> listComment = commentPostPort.getListCommentByUserId(user.getUserId(), getCommentRequest);

    return listComment.stream().map(
            comment -> {
              Post post = postPort.findPostByPostId(comment.getPostId());
              if (post == null) {
                return null;
              }
              if (comment.getParentCommentId() != null) {
                Comment parentComment = commentPostPort.findCommentById(comment.getParentCommentId());
                User userParentComment = authPort.getUserById(parentComment.getUserId());
                if (parentComment == null) {
                  return null;
                }
                if (friendShipPort.isBlock(user.getUserId(), post.getUserId())
                        || post.getPostStatus().equals(EPostStatus.PRIVATE)
                        || (post.getPostStatus().equals(EPostStatus.FRIEND) && !friendShipPort.isFriend(user.getUserId(), post.getUserId()))) {
                  return ActivityInteractionResponse.builder()
                          .owner(post.getUserId().equals(parentComment.getUserId())
                                  ? UserBasicDto.builder()
                                  .userId(userParentComment.getUserId())
                                  .firstName(userParentComment.getFirstName())
                                  .lastName(userParentComment.getLastName())
                                  .build()
                                  : userMapper.userToUserBasicDto(userParentComment)
                          )
                          .role("comment")
                          .createAt(comment.getCreateAt())
                          .build();

                }
                if (friendShipPort.isBlock(user.getUserId(), userParentComment.getUserId())) {
                  return ActivityInteractionResponse.builder()
                          .owner(UserBasicDto.builder()
                                  .userId(userParentComment.getUserId())
                                  .firstName(userParentComment.getFirstName())
                                  .lastName(userParentComment.getLastName())
                                  .build())
                          .roleId(comment.getCommentId())
                          .role("comment")
                          .createAt(comment.getCreateAt())
                          .build();
                }
                return commentMapper.commentToCommentActivityResponse(comment, userParentComment, post, "comment");
              }
              User ownerPost = authPort.getUserById(post.getUserId());
              if (friendShipPort.isBlock(user.getUserId(), post.getUserId())
                      || post.getPostStatus().equals(EPostStatus.PRIVATE)
                      || (post.getPostStatus().equals(EPostStatus.FRIEND) && !friendShipPort.isFriend(user.getUserId(), post.getUserId()))) {
                return ActivityInteractionResponse.builder()
                        .owner(UserBasicDto.builder()
                                .userId(ownerPost.getUserId())
                                .firstName(ownerPost.getFirstName())
                                .lastName(ownerPost.getLastName())
                                .build())
                        .role("post")
                        .createAt(comment.getCreateAt())
                        .build();
              }
              return commentMapper.commentToCommentActivityResponse(comment, ownerPost, post, "post");
            }
    ).filter(Objects::nonNull).collect(Collectors.toList());
  }

  private CommentResponse processCommentWithChildren(Comment comment, List<Comment> listComment) {
    User authComment = authPort.getUserById(comment.getUserId());
    CommentResponse response = commentMapper.commentToCommentResponse(comment, userMapper.userToUserBasicDto(authComment));
    if (comment.getCommentId() == null) {
      throw new CustomException("Comment is not found", HttpStatus.NOT_FOUND);
    }

    return response;
  }

  @Override
  public CommentResponse getCommentById(Long commentId) {
    Comment comment = commentPostPort.findCommentById(commentId);
    if (comment == null) {
      throw new CustomException("Comment not found", HttpStatus.NOT_FOUND);
    }
    Post post = postPort.findPostByPostId(comment.getPostId());
    checkCommentValid(post, getUserAuth());
    UserBasicDto userBasicDto = userMapper.userToUserBasicDto(authPort.getUserById(comment.getUserId()));

    return commentMapper.commentToCommentResponse(comment, userBasicDto);
  }

  @Override
  public List<CommentResponse> getCommentChildByParentId(Long id, GetCommentRequest getCommentRequest) {
    Comment commentParent = commentPostPort.findCommentById(id);
    if (commentParent == null) {
      throw new CustomException("Comment not found", HttpStatus.NOT_FOUND);
    }
    Post post = postPort.findPostByPostId(commentParent.getPostId());
    User user = authPort.getUserAuthOrDefaultVirtual();
    checkCommentValid(post, user);
    if (friendShipPort.isBlock(user.getUserId(), commentParent.getUserId())) {
      throw new CustomException("Not allow comment", HttpStatus.FORBIDDEN);
    }

    List<Long> blockIds = friendShipPort.getListBlockBoth(user.getUserId());
    return commentPostPort.getListCommentByParentId(id, blockIds, getCommentRequest).stream().map(
            comment -> {
              User userAuth = authPort.getUserById(comment.getUserId());
              return commentMapper.commentToCommentResponse(comment, userMapper.userToUserBasicDto(userAuth));
            }
    ).toList();
  }

  @Override
  public MessageResponse deleteComment(Long commentId) {
    Comment commentEntity = commentPostPort.findCommentById(commentId);

    if (commentEntity == null || !Objects.equals(commentEntity.getUserId(), this.getUserAuth().getUserId())) {
      throw new CustomException("Comment not found", HttpStatus.NOT_FOUND);
    }

    Post post = postPort.findPostByPostId(commentEntity.getPostId());
    checkCommentValid(post, getUserAuth());
    if (commentEntity == null || !Objects.equals(commentEntity.getUserId(), this.getUserAuth().getUserId())) {
      throw new CustomException("Comment not found", HttpStatus.NOT_FOUND);
    }
    try {
      Long commentParentId = commentEntity.getParentCommentId();
      commentPostPort.deleteCommentById(commentId);
      if (commentParentId != null) {
        commentPostPort.decreaseCommentCount(commentParentId, 1L);
      }
      postPort.decrementCommentQuantity(commentEntity.getPostId(), commentEntity.getRepliesQuantity() + 1);
      return new MessageResponse("Comment deleted successfully");
    } catch (CustomException e) {
      throw new CustomException("Failed to delete comment", HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public CommentResponse updateComment(Long commentId, CommentRequest comment) throws TranslateException, OrtException {
    Post post = postPort.findPostByPostId(comment.getPostId());
    User user = this.getUserAuth();
    checkCommentValid(post, user);
    boolean isToxic = phoBERTPortInput.isToxic(comment.getContent());

    if (isToxic) {
      throw new CustomException("Comment is toxic", HttpStatus.BAD_REQUEST);
    }

    Comment updatedComment = commentPostPort.findCommentById(commentId);

    if (updatedComment == null) {
      throw new CustomException("Comment not found", HttpStatus.NOT_FOUND);
    }

    String imageComment = getImageUrlCommentInRedis(comment.getPublicId(), this.getUserAuth());
    if (imageComment != null) {
      updatedComment.setImageUrl(imageComment);
    }

    updatedComment.setContent(comment.getContent());
    UserBasicDto userBasicDto = userMapper.userToUserBasicDto(user);

    commentPostPort.saveComment(updatedComment);
    return commentMapper.commentToCommentResponse(updatedComment, userBasicDto);
  }


  @Override
  public List<InteractionResponse> getCommentsByInteractions(GetCommentRequest getCommentRequest) {
    String role = "COMMENT";
    User currentUser = authPort.getUserAuth();
    List<InteractionResponse> interactionResponseList = new ArrayList<>();
//    commentPostPort.findCommentsByInteractions(authPort.getUserAuth().getUserId()).stream().forEach(
//            c -> {
//              String content = "You do not have sufficient permissions to view this content.";
//              String imageUrl = "";
//              if (!friendShipPort.isBlock(c.getUserId(), currentUser.getUserId())) {
//                content = c.getContent();
//                imageUrl = c.getImageUrl();
//              }
//              InteractionResponse interactionResponse = InteractionResponse.builder()
//                      .roleId(c.getCommentId())
//                      .role(role)
//                      .owner(userMapper.userToUserBasicDto(authPort.getUserById(c.getUserId())))
//                      .reactionType(reactionPostPort.findReactionCommentByCommentIdAndUserId(
//                              c.getCommentId(), currentUser.getUserId()
//                      ).getReactionType())
//                      .content(content)
//                      .image(imageUrl)

//                      .createAt(c.getCreateAt())
//                      .updateAt(null)
//                      .build();
//              interactionResponseList.add(interactionResponse);
//            }
//    );

    return interactionResponseList;
  }
//
//  @Override
//  public ReactionResponse handleReactionComment(Long commentId, String reactionType) {
//    Post post = postPort.findPostByPostId(commentId);
//    Comment updatedComment = commentPostPort.findCommentById(commentId);
//    User userUpdateComment = authPort.getUserById(updatedComment.getUserId());
//    checkCommentValid(post, userUpdateComment);
//
//    EReactionType newReactionType;
//    try {
//      newReactionType = EReactionType.valueOf(reactionType.toUpperCase());
//    } catch (IllegalArgumentException e) {
//      throw new CustomException("Invalid reaction type", HttpStatus.BAD_REQUEST);
//    }
//
//    ReactionPost reactionComment = commentPostPort.findByCommentIdAndUserID(commentId, this.getUserAuth().getUserId());
//    if (reactionComment == null) {
//      ReactionPost newReactionComment = new ReactionPost(
//              newReactionType,
//              updatedComment.getCommentId(),
//              this.getUserAuth().getUserId()
//      );
//      ReactionPost reactionPost = reactionPostPort.saveReaction(newReactionComment);
//      return reactionCommentMapper.commentToResponse(reactionPost);
//    }
//
//    reactionComment.setReactionType(newReactionType);
//    return reactionCommentMapper.commentToResponse(reactionPostPort.saveReaction(reactionComment));
//  }

//  @Override
//  public List<ReactionResponse> getAllReactionInComment(Long commentId) {
//    return List.of();
//  }
//

  private String getImageUrlCommentInRedis(String publicId, User userSave) {
    String tail = ImagePostInput.COMMENT_TAIL + userSave.getUserEmail();
    publicId += tail;
    if (redisImageTemplatePort.existsByKey(publicId)) {
      String value = redisImageTemplatePort.findByKey(publicId);
      if (value.equals(ImagePostInput.VALUE_LOADING)) {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
      imagePostPort.deleteAllImageRedisByTail(tail);
      return value.equals(ImagePostInput.VALUE_LOADING) ? null : value;
    }
    return null;
  }

}
