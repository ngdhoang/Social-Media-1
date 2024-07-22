package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.infrastructure.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.CommentResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
  CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

  @Mapping(source = "userEntity.userId", target = "userId")
  @Mapping(source = "post.postId", target = "postId")
  @Mapping(source = "parentComment.commentId", target = "parentCommentId")
  @Mapping(target = "childComments", expression = "java(mapChildComments(comment.getChildComments()))")
  @Mapping(target = "image", expression = "java(getFirstImageUrl(comment))")
  CommentResponse commentToCommentResponse(CommentEntity comment);

  List<CommentResponse> mapChildComments(List<CommentEntity> childComments);

  default String getFirstImageUrl(CommentEntity comment) {
    return Optional.ofNullable(comment.getImageComments())
            .orElse(Collections.emptyList())
            .stream()
            .findFirst()
            .map(imageComment -> imageComment.getImageUrl())
            .orElse(null);
  }

  @AfterMapping
  default void setAdditionalFields(CommentEntity comment, @MappingTarget CommentResponse commentResponse) {
    if (comment.getImageComments() != null && !comment.getImageComments().isEmpty()) {
      commentResponse.setImage(comment.getImageComments().get(0).getImageUrl());
    }
  }
}
