package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.ReactionPostInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.post.CommentPostPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.application.port.output.post.ReactionPostPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.post.EPostStatus;
import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.ReactionPost;
import com.GHTK.Social_Network.domain.model.post.comment.Comment;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.*;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionCountDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionUserDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ReactionRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ActivityInteractionResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionPostResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReactionPostService implements ReactionPostInput {
  private final ReactionPostPort reactionPostPort;
  private final PostPort postPort;
  private final AuthPort authPort;
  private final FriendShipPort friendShipPort;
  private final CommentPostPort commentPostPort;

  private final ReactionPostMapper reactionPostMapper;
  private final ReactionInfoMapper reactionInfoMapper;
  private final ReactionPostResponseMapper reactionPostResponseMapper;
  private final CommentMapper commentMapper;
  private final UserMapper userMapper;

  private void validatePostAccess(Post post, User user) {
    if (post == null) {
      throw new CustomException("Post not found", HttpStatus.NOT_FOUND);
    }

    Long postOwnerId = post.getUserId();
    User postOwner = authPort.getUserById(postOwnerId);
    Long currentUserId = user.getUserId();

    if (currentUserId.equals(postOwnerId)) {
      return;
    }

    if (!postOwner.getIsProfilePublic()
            || post.getPostStatus() == EPostStatus.PRIVATE
            || (post.getPostStatus() == EPostStatus.FRIEND && (currentUserId.equals(0) || !friendShipPort.isFriend(postOwnerId, currentUserId)))
            || friendShipPort.isBlock(postOwnerId, currentUserId)) {
      throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
    }
  }

  @Override
  public ReactionResponse handleReactionPost(Long postId, ReactionRequest reactionPostRequest) {
    User user = authPort.getUserAuthOrDefaultVirtual();

    Post post = postPort.findPostByPostId(postId);
    validatePostAccess(post, user);

    EReactionType newReactionType;
    try {
      newReactionType = reactionPostRequest.getReactionType() == null ? null : EReactionType.valueOf(reactionPostRequest.getReactionType());
    } catch (IllegalArgumentException e) {
      throw new CustomException("Invalid reaction type", HttpStatus.BAD_REQUEST);
    }
    ReactionPost reactionPost;

    reactionPost = reactionPostPort.findByPostIdAndUserID(postId, user.getUserId());

    if (reactionPost == null) {
      ReactionPost newReactionPost = ReactionPost.builder()
              .postId(postId)
              .userId(user.getUserId())
              .reactionType(newReactionType)
              .build();
      ReactionPost savedReactionPost = reactionPostPort.saveReaction(newReactionPost);

      postPort.incrementReactionQuantity(postId);
      return reactionPostMapper.postToResponse(savedReactionPost);
    } else {
      if (reactionPost.getReactionType().equals(newReactionType) || newReactionType == null) {
        reactionPostPort.deleteReaction(reactionPost);
        postPort.decrementReactionQuantity(postId);

        return null;
      } else {
        reactionPost.setReactionType(newReactionType);
        return reactionPostMapper.postToResponse(reactionPostPort.saveReaction(reactionPost));
      }
    }
  }

  @Override
  public ReactionPostResponse getListReactionInPost(Long postId, GetReactionPostRequest getReactionPostRequest) {
    Post post = postPort.findPostByPostId(postId);
    User user = authPort.getUserAuthOrDefaultVirtual();
    validatePostAccess(post, user);

    List<Map<EReactionType, Set<ReactionPost>>> reactionGroup = reactionPostPort.getReactionGroupByPostId(postId);
    List<Long> blockIds = friendShipPort.getListBlockBoth(user.getUserId());

    List<ReactionPost> reactionPosts = reactionPostPort.getListReactionByPostIdAndListBlock(postId, getReactionPostRequest, blockIds);
    List<ReactionUserDto> reactionUserDtos = reactionPosts.stream().map(
            reactionPost -> {
              User userReact = authPort.getUserById(reactionPost.getUserId());
              EReactionType reactionType = reactionPost.getReactionType();
              return reactionInfoMapper.toReactionInfoResponse(userReact, reactionType);
            }
    ).toList();

    return reactionPostResponseMapper.toReactionPostResponse(postId, reactionUserDtos,
            reactionGroup.stream().map(
                    entry -> ReactionCountDto.builder()
                            .type(entry.keySet().stream().findFirst().orElse(null))
                            .quantity((long) entry.values().stream().findFirst().orElse(null).size())
                            .build()
            ).toList()
    );
  }

  @Override
  public List<ActivityInteractionResponse> getListReactionInteractions(GetPostRequest getPostRequest) {
    User user = authPort.getUserAuthOrDefaultVirtual();
    List<Object[]> reactionInteractions = reactionPostPort.getListReactionInteractions(user.getUserId(), getPostRequest);
    if (reactionInteractions != null) {
      return reactionInteractions.stream().map(
              reactionInteraction -> {
                Long roleId = (Long) reactionInteraction[0];
                EReactionType reactionType = EReactionType.valueOf((String) reactionInteraction[1]);
                Long targetId = (Long) reactionInteraction[2];
                Date createAtDate = (Date) reactionInteraction[3];
                Instant createAt = createAtDate.toInstant();
                String role = (String) reactionInteraction[4];
                if (role.equals("post")) {
                  Post post = postPort.findPostByPostId(targetId);
                  User owner = authPort.getUserById(post.getUserId());
                  if (friendShipPort.isBlock(user.getUserId(), post.getUserId())
                          || post.getPostStatus().equals(EPostStatus.PRIVATE)
                          || (post.getPostStatus().equals(EPostStatus.FRIEND) && !friendShipPort.isFriend(user.getUserId(), post.getUserId()))) {
                    return ActivityInteractionResponse.builder()
                            .owner(UserBasicDto.builder()
                                    .userId(owner.getUserId())
                                    .firstName(owner.getFirstName())
                                    .lastName(owner.getLastName())
                                    .build())
                            .role(role)
                            .createAt(createAt)
                            .build();
                  }
                  if (friendShipPort.isBlock(user.getUserId(), owner.getUserId())) {
                    return ActivityInteractionResponse.builder()
                            .owner(UserBasicDto.builder()
                                    .userId(owner.getUserId())
                                    .firstName(owner.getFirstName())
                                    .lastName(owner.getLastName())
                                    .build())
                            .roleId(roleId)
                            .role(role)
                            .createAt(createAt)
                            .build();
                  }
                  return reactionPostMapper.reactionToReactionActivityResponse(roleId, reactionType, null, createAt, null, owner, post, role);
                }
                Comment comment = commentPostPort.findCommentById(targetId);
                if (comment != null) {
                  User owner = authPort.getUserById(comment.getUserId());
                  Post post = postPort.findPostByPostId(comment.getPostId());
                  if (friendShipPort.isBlock(user.getUserId(), post.getUserId())
                          || post.getPostStatus().equals(EPostStatus.PRIVATE)
                          || (post.getPostStatus().equals(EPostStatus.FRIEND) && !friendShipPort.isFriend(user.getUserId(), post.getUserId()))) {
                    return ActivityInteractionResponse.builder()
                            .owner(post.getUserId().equals(owner.getUserId())
                                    ? UserBasicDto.builder()
                                    .userId(owner.getUserId())
                                    .firstName(owner.getFirstName())
                                    .lastName(owner.getLastName())
                                    .build()
                                    : userMapper.userToUserBasicDto(owner))
                            .role(role)
                            .createAt(createAt)
                            .build();
                  }
                  if (friendShipPort.isBlock(user.getUserId(), owner.getUserId())) {
                    return ActivityInteractionResponse.builder()
                            .owner(UserBasicDto.builder()
                                    .userId(owner.getUserId())
                                    .firstName(owner.getFirstName())
                                    .lastName(owner.getLastName())
                                    .build())
                            .roleId(roleId)
                            .role(role)
                            .createAt(createAt)
                            .build();
                  }
                  return reactionPostMapper.reactionToReactionActivityResponse(roleId, reactionType, comment.getImageUrl(), createAt, comment.getParentCommentId(), owner, post, role);

                }
                return null;

              }
      ).toList();
    }
    return List.of();
  }
}