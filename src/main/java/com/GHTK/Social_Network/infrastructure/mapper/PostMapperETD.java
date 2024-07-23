package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.post.EPostStatus;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
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
}