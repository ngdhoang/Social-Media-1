package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.Comment;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.CommentResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface CommentMapper {
  @Mapping(target = "image", ignore = true)
  CommentResponse commentToCommentResponse(Comment comment);

  @Mapping(target = "isDelete", ignore = true)
  Comment commentResponseToComment(CommentResponse commentResponse);
}