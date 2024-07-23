package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.Comment;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CommentMapperETD {

  @Mapping(source = "userEntity.userId", target = "userId")
  @Mapping(source = "postEntity.postId", target = "postId")
  @Mapping(source = "parentCommentEntity", target = "parentCommentId", qualifiedByName = "parentCommentToId")
  Comment toDomain(CommentEntity entity);

  @Mapping(source = "userId", target = "userEntity.userId")
  @Mapping(source = "postId", target = "postEntity.postId")
  @Mapping(source = "parentCommentId", target = "parentCommentEntity", qualifiedByName = "idToParentComment")
  @Mapping(target = "childCommentEntities", ignore = true)
  @Mapping(target = "imageCommentEntities", ignore = true)
  @Mapping(target = "reactionCommentEntities", ignore = true)
  CommentEntity toEntity(Comment model);

  @Named("parentCommentToId")
  default Long parentCommentToId(CommentEntity parentComment) {
    return parentComment != null ? parentComment.getCommentId() : null;
  }

  @Named("idToParentComment")
  default CommentEntity idToParentComment(Long parentCommentId) {
    if (parentCommentId == null) {
      return null;
    }
    CommentEntity parentComment = new CommentEntity();
    parentComment.setCommentId(parentCommentId);
    return parentComment;
  }
}