package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.CommentPostInput;
import com.GHTK.Social_Network.application.port.input.post.ImagePostInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.post.CommentPostPort;
import com.GHTK.Social_Network.application.port.output.post.ImagePostPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.application.port.output.post.RedisImageTemplatePort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.comment.Comment;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.CommentMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.UserMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.CommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    private User getUserAuth() {
        User user = authPort.getUserAuth();
        return user == null ? User.builder().userId(0L).build() : user;
    }

    private void checkCommentValid(Post post, User u) {
        if (post == null) {
            throw new CustomException("Post not found", HttpStatus.NOT_FOUND);
        }

        if (friendShipPort.isBlock(u.getUserId(), post.getUserId()) || (!authPort.getUserById(post.getUserId()).getIsProfilePublic() && !u.getUserId().equals(this.getUserAuth().getUserId()))) {
            throw new CustomException("You are not allowed to create a comment", HttpStatus.FORBIDDEN);
        }

        if (friendShipPort.isFriend(post.getUserId(), u.getUserId())) {
            throw new CustomException("You are not allowed to create a comment", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public CommentResponse createCommentRoot(CommentRequest comment) {
        Post post = postPort.findPostByPostId(comment.getPostId());
        User user = getUserAuth();
        checkCommentValid(post, user);

        String imageCommentUrl = getImageUrlCommentInRedis(comment.getPublicId(), getUserAuth());
        Comment newComment = new Comment(
                LocalDate.now(),
                comment.getContent(),
                user.getUserId(),
                post.getPostId(),
                imageCommentUrl
        );
        Comment saveComment = commentPostPort.saveComment(newComment);
        postPort.incrementCommentQuantity(post.getPostId());
        UserBasicDto userBasicDto = userMapper.userToUserBasicDto(user);

        return commentMapper.commentToCommentResponse(saveComment, userBasicDto);
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

        if (friendShipPort.isBlock(user.getUserId(), parentComment.getUserId())) {
            throw new CustomException("You are not allowed to create a comment", HttpStatus.FORBIDDEN);
        }

        String imageComment = getImageUrlCommentInRedis(comment.getPublicId(), this.getUserAuth());
        Comment newComment = new Comment(
                LocalDate.now(),
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
        UserBasicDto userBasicDto = userMapper.userToUserBasicDto(user);

        return commentMapper.commentToCommentResponse(newComment, userBasicDto);
    }

    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        Post post = postPort.findPostByPostId(postId);
        checkCommentValid(post, this.getUserAuth());

        List<Comment> allComments = commentPostPort.findCommentByPostId(postId);

        return allComments.stream()
                .filter(comment -> comment.getParentCommentId() == null)
                .filter(c -> !friendShipPort.isBlock(c.getUserId(), getUserAuth().getUserId()))
                .map(rootComment ->
                        processCommentWithChildren(rootComment, allComments)
                )
                .collect(Collectors.toList());
    }

    private CommentResponse processCommentWithChildren(Comment comment, List<Comment> allComments) {
        User authComment = authPort.getUserById(comment.getUserId());
        CommentResponse response = commentMapper.commentToCommentResponse(comment, userMapper.userToUserBasicDto(authComment));
        if (comment.getCommentId() == null) {
            throw new CustomException("Comment is not found", HttpStatus.NOT_FOUND);
        }

//        List<CommentResponse> childResponses = allComments.stream()
//                .filter(c -> c.getParentCommentId() != null && c.getParentCommentId().equals(comment.getCommentId()))
//                .map(childComment -> processCommentWithChildren(childComment, allComments))
//                .toList();

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
        UserBasicDto userBasicDto = userMapper.userToUserBasicDto(getUserAuth());

        return commentMapper.commentToCommentResponse(comment, userBasicDto);
    }

    @Override
    public List<CommentResponse> getAllCommentChildById(Long id) {
        return commentPostPort.findCommentByParentId(id).stream().map(
                comment -> commentMapper.commentToCommentResponse(comment, userMapper.userToUserBasicDto(getUserAuth()))
        ).toList();
    }

    @Override
    public MessageResponse deleteComment(Long commentId) {
        Comment commentEntity = commentPostPort.findCommentById(commentId);
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
    public CommentResponse updateComment(Long commentId, CommentRequest comment) {
        Post post = postPort.findPostByPostId(comment.getPostId());
        User user = this.getUserAuth();
        checkCommentValid(post, user);

        Comment updatedComment = commentPostPort.findCommentById(commentId);
        String imageComment = getImageUrlCommentInRedis(comment.getPublicId(), this.getUserAuth());
        if (imageComment != null) {
            updatedComment.setImageUrl(imageComment);
        }

        updatedComment.setContent(comment.getContent());
        UserBasicDto userBasicDto = userMapper.userToUserBasicDto(user);

        commentPostPort.saveComment(updatedComment);
        return commentMapper.commentToCommentResponse(updatedComment, userBasicDto);
    }

//  @Override
//  public List<InteractionResponse> getCommentsByInteractions() {
//    String role = "COMMENT";
//    User currentUser = authPort.getUserAuth();
//    List<InteractionResponse> interactionResponseList = new ArrayList<>();
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
//                      .createdAt(c.getCreateAt())
//                      .updateAt(null)
//                      .build();
//              interactionResponseList.add(interactionResponse);
//            }
//    );
//    return interactionResponseList;
//  }
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
