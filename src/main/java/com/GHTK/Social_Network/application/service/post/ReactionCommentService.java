package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.ReactionCommentInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.post.CommentPostPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.application.port.output.post.ReactionCommentPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.post.EPostStatus;
import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.ReactionComment;
import com.GHTK.Social_Network.domain.model.post.comment.Comment;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ReactionCommentMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ReactionCommentResponseMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ReactionInfoMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionCountDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionUserDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionCommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ReactionRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionCommentResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReactionCommentService implements ReactionCommentInput {
  private final ReactionCommentPort reactionCommentPort;
  private final PostPort postPort;
  private final AuthPort authPort;
  private final FriendShipPort friendShipPort;
  private final CommentPostPort commentPostPort;

  private final ReactionCommentMapper reactionCommentMapper;
  private final ReactionInfoMapper reactionInfoMapper;
  private final ReactionCommentResponseMapper reactionCommentResponseMapper;

  private User getUserAuth() {
    User user = authPort.getUserAuth();
    if (user == null) throw new CustomException("User not found", HttpStatus.NOT_FOUND);
    return user;
  }

  private void validateCommentAccess(Comment comment, User user) {
    if (comment == null) {
      throw new CustomException("Comment not found", HttpStatus.NOT_FOUND);
    }

    Post post = postPort.findPostByPostId(comment.getPostId());

    Long postOwnerId = post.getUserId();
    User postOwner = authPort.getUserById(postOwnerId);
    Long currentUserId = user.getUserId();

    if (currentUserId.equals(postOwnerId)) {
      return;
    }

    if (!postOwner.getIsProfilePublic()
            || post.getPostStatus() == EPostStatus.PRIVATE
            || (post.getPostStatus() == EPostStatus.FRIEND && (currentUserId.equals(0) || !friendShipPort.isFriend(postOwnerId, currentUserId)))
            || friendShipPort.isBlock(postOwnerId, currentUserId)
            || friendShipPort.isBlock(postOwnerId, comment.getUserId())
    ) {
      throw new CustomException("Access deny", HttpStatus.FORBIDDEN);
    }

    Long commentParentId = comment.getParentCommentId();
    if (commentParentId != null) {
      Comment parentComment = commentPostPort.findCommentById(commentParentId);
      if (parentComment == null) {
        throw new CustomException("Parent comment not found", HttpStatus.NOT_FOUND);
      }

      if (friendShipPort.isBlock(postOwnerId, parentComment.getUserId()) || (!currentUserId.equals(0) && friendShipPort.isBlock(currentUserId, parentComment.getUserId()))) {
        throw new CustomException("Access deny", HttpStatus.FORBIDDEN);
      }
    }
  }

  @Override
  public ReactionResponse handleReactionComment(Long commentId, ReactionRequest reactionCommentRequest) {
    User user = authPort.getUserAuthOrDefaultVirtual();

    Comment comment = commentPostPort.findCommentById(commentId);
    validateCommentAccess(comment, user);

    EReactionType newReactionType;
    try {
      newReactionType = reactionCommentRequest.getReactionType() == null ? null : EReactionType.valueOf(reactionCommentRequest.getReactionType());
    } catch (IllegalArgumentException e) {
      throw new CustomException("Invalid reaction type", HttpStatus.BAD_REQUEST);
    }
    ReactionComment reactionComment;

    reactionComment = reactionCommentPort.findByCommentIdAndUserID(commentId, getUserAuth().getUserId());

    if (reactionComment == null) {
      ReactionComment newReactionComment = ReactionComment.builder()
              .commentId(commentId)
              .userId(user.getUserId())
              .reactionType(newReactionType)
              .build();
      ReactionComment savedReactionComment = reactionCommentPort.saveReaction(newReactionComment);

      commentPostPort.increaseReactionCount(commentId);

      return reactionCommentMapper.commentToResponse(savedReactionComment);
    } else {
      if (reactionComment.getReactionType() == newReactionType || newReactionType == null) {
        reactionCommentPort.deleteReaction(reactionComment);
        Long commentParentId = comment.getParentCommentId();
        if (commentParentId != null)
          commentPostPort.decreaseReactionCount(commentParentId, 1L);

        return null;
      } else {
        reactionComment.setReactionType(newReactionType);
        return reactionCommentMapper.commentToResponse(reactionCommentPort.saveReaction(reactionComment));
      }
    }
  }

  @Override
  public ReactionCommentResponse getListReactionInComment(Long commentId, GetReactionCommentRequest getReactionCommentRequest) {
    User user = authPort.getUserAuthOrDefaultVirtual();

    Comment comment = commentPostPort.findCommentById(commentId);

    validateCommentAccess(comment, user);
    List<Map<EReactionType, Set<ReactionComment>>> reactionGroup = reactionCommentPort.getReactionGroupByCommentId(commentId);
    List<Long> blockIds = friendShipPort.getListBlockBoth(user.getUserId());

    List<ReactionComment> reactionComments = reactionCommentPort.getListReactionByCommentIdAndListBlock(commentId, getReactionCommentRequest, blockIds);
    List<ReactionUserDto> reactionCommentUserDtos = reactionComments.stream().map(
            reactionComment -> {
              User userReact = authPort.getUserById(reactionComment.getUserId());
              EReactionType reactionType = reactionComment.getReactionType();
              return reactionInfoMapper.toReactionInfoResponse(userReact, reactionType);
            }
    ).toList();
    return reactionCommentResponseMapper.toReactionCommentResponse(commentId, reactionCommentUserDtos,
            reactionGroup.stream().map(
                    entry -> ReactionCountDto.builder()
                            .type(entry.keySet().stream().findFirst().orElse(null))
                            .quantity((long) entry.values().stream().findFirst().orElse(null).size())
                            .build()
            ).toList()
    );

  }

}