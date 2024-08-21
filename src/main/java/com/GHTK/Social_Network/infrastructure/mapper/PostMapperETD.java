package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.post.EPostStatus;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.PostNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface PostMapperETD {
  @Mapping(source = "userEntity.userId", target = "userId")
  Post toDomain(PostEntity postEntity);

  @Mapping(source = "userId", target = "userEntity.userId")
  @Mapping(target = "imagePostEntities", ignore = true)
  @Mapping(target = "tagUserEntities", ignore = true)
  @Mapping(target = "reactionPostEntities", ignore = true)
  @Mapping(target = "commentEntities", ignore = true)
  PostEntity toEntity(Post post);

  EPostStatus postStatusEntityToPostStatus(EPostStatusEntity postStatusEntity);

  EPostStatusEntity postStatusToPostStatusEntity(EPostStatus postStatus);


  @Mapping(source = "postId", target = "postId")
  @Mapping(source = "postStatus", target = "postStatus")
  @Mapping(source = "createAt", target = "createAt", qualifiedByName = "mapInstantToLocalDateTime")
  PostNode postEntityToNode(PostEntity post);

  @Named("mapInstantToLocalDateTime")
  public static LocalDateTime mapInstantToLocalDateTime(Instant instant) {
    if (instant == null) {
      return null;
    }
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
  }


  @Mapping(source = "postId", target = "postId")
  @Mapping(source = "postStatus", target = "postStatus")
  @Mapping(source = "createAt", target = "createAt", qualifiedByName = "mapInstantToLocalDateTime")
  PostNode postDomainToNode(Post post);

}