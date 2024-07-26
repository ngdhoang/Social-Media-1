package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.ReactionPostInput;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.application.port.output.post.ReactionPostPort;
import com.GHTK.Social_Network.domain.model.EReactionType;
import com.GHTK.Social_Network.domain.model.ReactionPost;
import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.domain.model.post.EPostStatus;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ReactionPostInfoMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ReactionPostMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ReactionPostResponseMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionPostCountDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionPostUserDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ReactionPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionPostResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReactionPostService implements ReactionPostInput {
  private User getUserAuth() {
    User user = authPort.getUserAuth();
    return user == null ? User.builder().userId(0L).build() : user;
  }

  private final ReactionPostPort reactionPostPort;
  private final PostPort postPort;
  private final AuthPort authPort;
  private final FriendShipPort friendShipPort;

  private final ReactionPostMapper reactionPostMapper;
  private final ReactionPostInfoMapper reactionPostInfoMapper;
  private final ReactionPostResponseMapper reactionPostResponseMapper;

  private void validatePostAccess(Post post) {
    if (post == null) {
      throw new CustomException("Post not found", HttpStatus.NOT_FOUND);
    }

    Long postOwnerId = post.getUserId();
    User postOwner = authPort.getUserById(postOwnerId);
    Long currentUserId = getUserAuth().getUserId();

    if (currentUserId.equals(postOwnerId)) {
      return;
    }

    if (!postOwner.getIsProfilePublic()) {
      throw new CustomException("Post not accessible", HttpStatus.FORBIDDEN);
    }

    if (friendShipPort.isBlock(postOwnerId, currentUserId)) {
      throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
    }


    if (post.getPostStatus() == EPostStatus.PRIVATE) {
      throw new CustomException("Post not accessible", HttpStatus.FORBIDDEN);
    }

    if (post.getPostStatus() == EPostStatus.FRIEND) {
      if (friendShipPort.isFriend(postOwnerId, currentUserId)) {
        return;
      }
    }

    return;
  }

  @Override
  public ReactionResponse handleReactionPost(Long postId, ReactionPostRequest reactionPostRequest) {
    User user = getUserAuth();
    Post post = postPort.findPostByPostId(postId);
    validatePostAccess(post);

    EReactionType newReactionType;
    try {
      newReactionType = reactionPostRequest.getReactionType() == null ? null : EReactionType.valueOf(reactionPostRequest.getReactionType());
    } catch (IllegalArgumentException e) {
      throw new CustomException("Invalid reaction type", HttpStatus.BAD_REQUEST);
    }
    ReactionPost reactionPost;
//    ReactionPostRedisDto reactionPostRedisDto = redisReactionPostPort.findByKey(postId.toString());
//        if (reactionPostRedisDto != null) {
//            reactionPost = reactionPostMapper.toReactionPost(reactionPostRedisDto);
//            if (reactionPost.getReactionType() == newReactionType) {
//                reactionPostPort.deleteReaction(reactionPost);
//                // delete from redis
//                return null;
//            } else {
//                reactionPost.setReactionType(newReactionType);
//                // save to redis
//                return reactionPostMapper.toReactionPostResponse(reactionPostPort.saveReaction(reactionPost));
//            }
//        }
    reactionPost = reactionPostPort.findByPostIdAndUserID(postId, getUserAuth().getUserId());

    if (reactionPost == null) {
      ReactionPost newReactionPost = ReactionPost.builder()
              .postId(postId)
              .userId(user.getUserId())
              .reactionType(newReactionType)
              .build();
      ReactionPost savedReactionPost = reactionPostPort.saveReaction(newReactionPost);

//      if (reactionPostRedisDto == null) {
//        // get reactionPost from db
//        // save to redis
//      } else {
//        // update redis by add a new reaction to type
//      }

//      redisReactionPostPort.createOrUpdate(savedReactionPost);
      postPort.incrementReactionQuantity(postId);
      return reactionPostMapper.postToResponse(savedReactionPost);
    } else {
      if (reactionPost.getReactionType() == newReactionType || newReactionType == null) {
        reactionPostPort.deleteReaction(reactionPost);
        postPort.incrementReactionQuantity(postId);

        // delete from redis
        return null;
      } else {
        reactionPost.setReactionType(newReactionType);
        // save to redis
        return reactionPostMapper.postToResponse(reactionPostPort.saveReaction(reactionPost));
      }
    }
  }

  @Override
  public List<ReactionResponse> getAllReactionInPost(Long postId) {
    return reactionPostPort.findByPostId(postId).stream().map(
            reactionPostMapper::postToResponse
    ).toList();
  }

  @Override
  public ReactionPostResponse getListReactionInPost(Long postId, GetReactionPostRequest getReactionPostRequest) {
    Post post = postPort.findPostByPostId(postId);

    validatePostAccess(post);
    List<Map<EReactionType, Set<ReactionPost>>> reactionGroup = reactionPostPort.getReactionGroupByPostId(postId);

    if (getReactionPostRequest.getReactionType() == null) {
      // check in redis data example: reaction-post:1 - {postId: 1, userId: 1, reactionPostObjectRedisDtos: [{userId: 1, reactionType: LIKE}, {userId: 2, reactionType: LOVE}]}

      // if not exist in redis, get from db
      List<ReactionPost> reactionPosts = reactionPostPort.getListReactionByPostId(postId, getReactionPostRequest);


      List<ReactionPostUserDto> reactionPostUserDtos = reactionPosts.stream().map(
              // get user info from db
              reactionPost -> {
                User userReact = authPort.getUserById(reactionPost.getUserId());
                EReactionType reactionType = reactionPost.getReactionType();
                return reactionPostInfoMapper.toReactionPostInfoResponse(userReact, reactionType);
              }
      ).toList();
      // save to redis list reaction of each type

      //[{LIKE=[ReactionPost(reactionPostId=3, postId=1, reactionType=LIKE, userId=2, createdAt=2024-07-24, updateAt=null), ReactionPost(reactionPostId=2, postId=1, reactionType=LIKE, userId=1, createdAt=2024-07-24, updateAt=null)]}, {LOVE=[ReactionPost(reactionPostId=4, postId=1, reactionType=LOVE, userId=3, createdAt=2024-07-24, updateAt=null)]}]
      return reactionPostResponseMapper.toReactionPostResponse(postId, reactionPostUserDtos,
              reactionGroup.stream().map(
                      entry -> ReactionPostCountDto.builder()
                              .type(entry.keySet().stream().findFirst().orElse(null))
                              .quantity((long) entry.values().stream().findFirst().orElse(null).size())
                              .build()
              ).toList()
      );
    }

    // get from db
    List<ReactionPost> reactionPosts = reactionPostPort.getByPostIdAndType(postId, getReactionPostRequest);
    List<ReactionPostUserDto> reactionPostUserDtos = reactionPosts.stream().map(
            // get user info from db
            reactionPost -> {
              User userReact = authPort.getUserById(reactionPost.getUserId());
              EReactionType reactionType = reactionPost.getReactionType();
              return reactionPostInfoMapper.toReactionPostInfoResponse(userReact, reactionType);
            }
    ).toList();
    // save to redis
    return reactionPostResponseMapper.toReactionPostResponse(postId, reactionPostUserDtos,
            reactionGroup.stream().map(
                    entry -> ReactionPostCountDto.builder()
                            .type(entry.keySet().stream().findFirst().orElse(null))
                            .quantity((long) entry.values().stream().findFirst().orElse(null).size())
                            .build()
            ).toList()
    );

  }

}