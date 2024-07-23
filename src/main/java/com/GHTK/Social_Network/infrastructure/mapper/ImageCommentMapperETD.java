package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.ImageComment;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.ImageCommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImageCommentMapperETD {
  @Mapping(source = "commentEntity.commentId", target = "commentId")
  @Mapping(source = "userEntity.userId", target = "userId")
  ImageComment toDomain(ImageCommentEntity entity);

  @Mapping(source = "commentId", target = "commentEntity.commentId")
  @Mapping(source = "userId", target = "userEntity.userId")
  ImageCommentEntity toEntity(ImageComment model);
}
