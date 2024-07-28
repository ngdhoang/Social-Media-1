package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.post.comment.Comment;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.CommentResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
  @Mapping(target = "user", source = "userBasicDto")
  CommentResponse commentToCommentResponse(Comment comment, UserBasicDto userBasicDto);

  Comment commentResponseToComment(CommentResponse commentResponse);

  @Mapping(target = "user", ignore = true)
  @Mapping(target = "repliesQuantity", expression = "java(Long.valueOf(childComments.size()))")
  CommentResponse commentToCommentResponse(Comment commentParent, List<Comment> childComments);

  List<CommentResponse> commentListToCommentResponseList(List<Comment> comments);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateCommentFromCommentResponse(CommentResponse commentResponse, @MappingTarget Comment comment);
}