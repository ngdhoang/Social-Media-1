package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.Comment;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.CommentResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
  @Mapping(target = "image", ignore = true)
  @Mapping(target = "childComments", ignore = true)
  CommentResponse commentToCommentResponse(Comment comment);

  @Mapping(target = "isDelete", ignore = true)
  Comment commentResponseToComment(CommentResponse commentResponse);

  @Mapping(target = "childComments", source = "childComments")
  @Mapping(target = "image", ignore = true)
  CommentResponse commentToCommentResponse(Comment commentParent, List<Comment> childComments);

  List<CommentResponse> commentListToCommentResponseList(List<Comment> comments);

  @AfterMapping
  default void setImage(@MappingTarget CommentResponse target, Comment source) {
    target.setImage(null);
  }

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateCommentFromCommentResponse(CommentResponse commentResponse, @MappingTarget Comment comment);
}