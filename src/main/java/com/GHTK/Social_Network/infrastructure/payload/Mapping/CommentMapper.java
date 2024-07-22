package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.CommentResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
  CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

  @Mapping(source = "user.userId", target = "userId")
  @Mapping(source = "post.postId", target = "postId")
  @Mapping(source = "parentComment.commentId", target = "parentCommentId")
  @Mapping(target = "childComments", expression = "java(mapChildComments(comment.getChildComments()))")
  @Mapping(target = "image", expression = "java(getFirstImageUrl(comment))")
  CommentResponse commentToCommentResponse(CommentEntity commentEntity);

  List<CommentResponse> mapChildComments(List<CommentEntity> childCommentEntities);

  default String getFirstImageUrl(CommentEntity commentEntity) {
    return Optional.ofNullable(commentEntity.getImageCommentEntities())
            .orElse(Collections.emptyList())
            .stream()
            .findFirst()
            .map(imageComment -> imageComment.getImageUrl())
            .orElse(null);
  }

  @AfterMapping
  default void setAdditionalFields(CommentEntity commentEntity, @MappingTarget CommentResponse commentResponse) {
    if (commentEntity.getImageCommentEntities() != null && !commentEntity.getImageCommentEntities().isEmpty()) {
      commentResponse.setImage(commentEntity.getImageCommentEntities().get(0).getImageUrl());
    }
  }
}
