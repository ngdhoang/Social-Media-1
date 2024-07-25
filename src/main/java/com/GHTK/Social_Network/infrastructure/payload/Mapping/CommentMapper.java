package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.Comment;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.CommentResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
  @Mapping(target = "imageUrl", source = "imageUrl")
  @Mapping(target = "childComments", ignore = true)
  CommentResponse commentToCommentResponse(Comment comment);

  Comment commentResponseToComment(CommentResponse commentResponse);

  @Mapping(target = "childComments", source = "childComments")
  @Mapping(target = "imageUrl", source = "commentParent.imageUrl")
  CommentResponse commentToCommentResponse(Comment commentParent, List<Comment> childComments);

  List<CommentResponse> commentListToCommentResponseList(List<Comment> comments);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateCommentFromCommentResponse(CommentResponse commentResponse, @MappingTarget Comment comment);
}