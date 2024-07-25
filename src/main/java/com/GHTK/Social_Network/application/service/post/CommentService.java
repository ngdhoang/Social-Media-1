package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.CommentPostInput;
import com.GHTK.Social_Network.application.port.input.post.ImagePostInput;
import com.GHTK.Social_Network.application.port.input.post.ReactionCommentPostInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.post.CommentPostPort;
import com.GHTK.Social_Network.application.port.output.post.ImagePostPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.application.port.output.post.RedisImageTemplatePort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.Comment;
import com.GHTK.Social_Network.domain.model.EReactionType;
import com.GHTK.Social_Network.domain.model.ReactionComment;
import com.GHTK.Social_Network.domain.model.User;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentPostInput, ReactionCommentPostInput {
  private final PostPort postPort;
  private final AuthPort authPort;
  private final CommentPostPort commentPostPort;
  private final FriendShipPort friendShipPort;
  private final RedisImageTemplatePort redisImageTemplatePort;
  private final ImagePostPort imagePostPort;

  private final CommentMapper commentMapper;
  private final ReactionCommentMapper reactionCommentMapper;

  private User getUserAuth() {
    User user = authPort.getUserAuth();
    return user == null ? User.builder().userId(0L).build() : user;
  }

  private void checkCommentValid(Post post, User u) {
    if (post == null) {
      throw new CustomException("Post not found", HttpStatus.NOT_FOUND);
    }

    if (friendShipPort.isBlock(u.getUserId(), post.getUserId()) || !authPort.getUserById(post.getUserId()).getIsProfilePublic() && !u.getUserId().equals(this.getUserAuth().getUserId())) {
      throw new CustomException("You are not allowed to create a comment", HttpStatus.FORBIDDEN);
    }
  }

  @Override
  public CommentResponse createCommentSrc(CommentRequest comment) {
    Post post = postPort.findPostByPostId(comment.getPostId());
    User user = getUserAuth();
    checkCommentValid(post, user);

    String imageCommentUrl = getImageUrlCommentInRedis(comment.getPublicId(), getUserAuth());
    Comment newComment = new Comment(
            new Date(),
            comment.getContent(),
            user.getUserId(),
            post.getUserId(),
            imageCommentUrl
    );
    Comment saveComment = commentPostPort.saveComment(newComment);

    return commentMapper.commentToCommentResponse(saveComment);
  }


  @Override
  public CommentResponse createCommentChild(Long commentIdSrc, CommentRequest comment) {
    Post post = postPort.findPostByPostId(comment.getPostId());
    User user = this.getUserAuth();
    checkCommentValid(post, user);
    Comment parentComment = commentPostPort.findCommentById(commentIdSrc);
    if (parentComment == null) {
      throw new CustomException("Parent comment not found", HttpStatus.NOT_FOUND);
    }

    String imageComment = getImageUrlCommentInRedis(comment.getPublicId(), this.getUserAuth());
    Comment newComment = new Comment(
            new Date(),
            comment.getContent(),
            user.getUserId(),
            post.getPostId(),
            imageComment
    );
    commentPostPort.setParentComment(commentIdSrc, newComment);
    newComment = commentPostPort.saveComment(newComment);

    return commentMapper.commentToCommentResponse(newComment);
  }

  @Override
  public List<CommentResponse> getCommentsByPostId(Long postId) {
    Post post = postPort.findPostByPostId(postId);
    checkCommentValid(post, this.getUserAuth());

    List<Comment> allComments = commentPostPort.findCommentByPostId(postId);

    return allComments.stream()
            .filter(comment -> comment.getParentCommentId() == null)
            .map(rootComment -> processCommentWithChildren(rootComment, allComments))
            .collect(Collectors.toList());
  }

  @Override
  public List<CommentResponse> getCommentsParentByPostId(Long postId) {
    return commentPostPort.findCommentByParentId(postId).stream().map(
            commentMapper::commentToCommentResponse
    ).toList();
  }

  private CommentResponse processCommentWithChildren(Comment comment, List<Comment> allComments) {
    CommentResponse response = commentMapper.commentToCommentResponse(comment);
    System.out.println(comment.getParentCommentId());
    if (comment.getCommentId() == null) {
      throw new CustomException("Comment is not fouled", HttpStatus.NOT_FOUND);
    }

    List<CommentResponse> childResponses = allComments.stream()
            .filter(c -> c.getParentCommentId() != null && c.getParentCommentId().equals(comment.getCommentId()))
            .map(childComment -> processCommentWithChildren(childComment, allComments))
            .collect(Collectors.toList());

    response.setChildComments(childResponses);
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
    return commentMapper.commentToCommentResponse(comment);
  }

  @Override
  public List<CommentResponse> getAllCommentChildById(Long id) {
    return commentPostPort.findCommentByParentId(id).stream().map(
            commentMapper::commentToCommentResponse
    ).toList();
  }

  @Override
  public MessageResponse deleteComment(Long commentId) {
    Comment commentEntity = commentPostPort.findCommentById(commentId);
    if (commentEntity == null || !Objects.equals(commentEntity.getUserId(), this.getUserAuth().getUserId())) {
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
    User user = this.getUserAuth();
    checkCommentValid(post, user);

    Comment updatedComment = commentPostPort.findCommentById(commentId);
    String imageComment = getImageUrlCommentInRedis(comment.getPublicId(), this.getUserAuth());
    if (imageComment != null) {
      updatedComment.setImageUrl(imageComment);
    }
    if (comment.getImageUrl() == null && comment.getPublicId() == null) {
      updatedComment.setImageUrl(null);
    }
    updatedComment.setContent(comment.getContent());

    commentPostPort.saveComment(updatedComment);
    return commentMapper.commentToCommentResponse(updatedComment);
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

    ReactionComment reactionComment = commentPostPort.findByCommentIdAndUserID(commentId, this.getUserAuth().getUserId());
    if (reactionComment == null) {
      ReactionComment newReactionComment = new ReactionComment(
              newReactionType,
              updatedComment.getCommentId(),
              this.getUserAuth().getUserId()
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


  private String getImageUrlCommentInRedis(String publicId, User userSave) {
    String tail = "_" + ImagePostInput.COMMENT_TAIL + "_" + userSave.getUserEmail();
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
