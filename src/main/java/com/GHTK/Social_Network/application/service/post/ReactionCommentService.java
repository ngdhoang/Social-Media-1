package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.ReactionCommentInput;
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
import com.GHTK.Social_Network.infrastructure.payload.Mapping.*;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionCountDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionUserDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionCommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ReactionRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionCommentResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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

    private void validateCommentAccess(Comment comment) {
        if (comment == null) {
            throw new CustomException("Comment not found", HttpStatus.NOT_FOUND);
        }

        Post post = postPort.findPostByPostId(comment.getPostId());

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
//    if (post.getPostStatus() == EPostStatus.FRIEND) {
//      if (friendShipPort.isFriend(postOwnerId, currentUserId)) {
//        return;
//      }
//    }
//
//    return;
    }

    @Override
    public ReactionResponse handleReactionComment(Long commentId, ReactionRequest reactionCommentRequest) {
        User user = getUserAuth();
        Comment comment = commentPostPort.findCommentById(commentId);
        validateCommentAccess(comment);

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

                // delete from redis
                return null;
            } else {
                reactionComment.setReactionType(newReactionType);
                // save to redis
                return reactionCommentMapper.commentToResponse(reactionCommentPort.saveReaction(reactionComment));
            }
        }
    }


    @Override
    public List<ReactionResponse> getAllReactionInComment(Long commentId) {
        return reactionCommentPort.findByCommentId(commentId).stream().map(
                reactionCommentMapper::commentToResponse
        ).toList();
    }

    @Override
    public ReactionCommentResponse getListReactionInComment(Long commentId, GetReactionCommentRequest getReactionCommentRequest) {
        Comment comment = commentPostPort.findCommentById(commentId);

        validateCommentAccess(comment);
        List<Map<EReactionType, Set<ReactionComment>>> reactionGroup = reactionCommentPort.getReactionGroupByCommentId(commentId);

        if (getReactionCommentRequest.getReactionType() == null) {
            // check in redis data example: reaction-post:1 - {commentId: 1, userId: 1, reactionCommentObjectRedisDtos: [{userId: 1, reactionType: LIKE}, {userId: 2, reactionType: LOVE}]}

            // if not exist in redis, get from db
            List<ReactionComment> reactionComments = reactionCommentPort.getListReactionByCommentId(commentId, getReactionCommentRequest);


            List<ReactionUserDto> reactionCommentUserDtos = reactionComments.stream().map(
                    // get user info from db
                    reactionComment -> {
                        User userReact = authPort.getUserById(reactionComment.getUserId());
                        EReactionType reactionType = reactionComment.getReactionType();
                        return reactionInfoMapper.toReactionInfoResponse(userReact, reactionType);
                    }
            ).toList();
            // save to redis list reaction of each type

            //[{LIKE=[ReactionComment(reactionCommentId=3, commentId=1, reactionType=LIKE, userId=2, createdAt=2024-07-24, updateAt=null), ReactionComment(reactionCommentId=2, commentId=1, reactionType=LIKE, userId=1, createdAt=2024-07-24, updateAt=null)]}, {LOVE=[ReactionComment(reactionCommentId=4, commentId=1, reactionType=LOVE, userId=3, createdAt=2024-07-24, updateAt=null)]}]
            return reactionCommentResponseMapper.toReactionCommentResponse(commentId, reactionCommentUserDtos,
                    reactionGroup.stream().map(
                            entry -> ReactionCountDto.builder()
                                    .type(entry.keySet().stream().findFirst().orElse(null))
                                    .quantity((long) entry.values().stream().findFirst().orElse(null).size())
                                    .build()
                    ).toList()
            );
        }

        // get from db
        List<ReactionComment> reactionComments = reactionCommentPort.getByCommentIdAndType(commentId, getReactionCommentRequest);
        List<ReactionUserDto> reactionCommentUserDtos = reactionComments.stream().map(
                // get user info from db
                reactionComment -> {
                    User userReact = authPort.getUserById(reactionComment.getUserId());
                    EReactionType reactionType = reactionComment.getReactionType();
                    return reactionInfoMapper.toReactionInfoResponse(userReact, reactionType);
                }
        ).toList();
        // save to redis
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