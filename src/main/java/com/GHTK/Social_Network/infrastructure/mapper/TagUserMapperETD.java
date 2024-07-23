package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.post.TagUser;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.TagUserEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagUserMapperETD {
  @Mapping(source = "postEntity.postId", target = "postId")
  @Mapping(source = "userEntity.userId", target = "userId")
  TagUser ToDomain(TagUserEntity tagUserEntity);

  @Mapping(source = "postId", target = "postEntity")
  @Mapping(source = "userId", target = "userEntity")
  TagUserEntity toEntity(TagUser tagUser);

  default PostEntity mapPostId(Long postId) {
    if (postId == null) {
      return null;
    }
    PostEntity postEntity = new PostEntity();
    postEntity.setPostId(postId);
    return postEntity;
  }

  default UserEntity mapUserId(Long userId) {
    if (userId == null) {
      return null;
    }
    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(userId);
    return userEntity;
  }
}
