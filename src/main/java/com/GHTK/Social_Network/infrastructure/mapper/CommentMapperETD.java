package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.post.comment.Comment;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.CommentNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

  @Mapping(source = "commentId", target = "commentId")
  @Mapping(source = "createAt", target = "createAt", qualifiedByName = "mapInstantToLocalDateTime")
  CommentNode commentEntityToNode(CommentEntity commentEntity);

  @Named("mapInstantToLocalDateTime")
  public static LocalDateTime mapInstantToLocalDateTime(Instant instant) {
    if (instant == null) {
      return null;
    }
    System.out.println(ZoneId.systemDefault());
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
  }
}