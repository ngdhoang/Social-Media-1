package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.comment.Comment;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.CommentBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.ActivityInteractionResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.CommentResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
  @Mapping(target = "user", source = "userBasicDto")
  CommentResponse commentToCommentResponse(Comment comment, UserBasicDto userBasicDto, EReactionType reactionType);

  Comment commentResponseToComment(CommentResponse commentResponse);

  @Mapping(target = "user", ignore = true)
  @Mapping(target = "repliesQuantity", expression = "java(Long.valueOf(childComments.size()))")
  CommentResponse commentToCommentResponse(Comment commentParent, List<Comment> childComments);

  List<CommentResponse> commentListToCommentResponseList(List<Comment> comments);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateCommentFromCommentResponse(CommentResponse commentResponse, @MappingTarget Comment comment);

  @Mapping(target = "owner", source = "user")
  @Mapping(target = "roleId", source = "comment.commentId")
  @Mapping(target = "post", source = "post")
  @Mapping(target = "role", source = "role")
  @Mapping(target = "createAt", source = "comment.createAt")
  @Mapping(target = "content", source = "comment.content")
  @Mapping(target = "parentCommentId", source = "comment.parentCommentId")
  @Mapping(target = "imageUrl", source = "comment.imageUrl")
  ActivityInteractionResponse commentToCommentActivityResponse(Comment comment, User user, Post post, String role);

  @Mapping(target = "commentId", source = "commentId")
  CommentBasicDto commentToCommentBasicDto(Comment comment);
}