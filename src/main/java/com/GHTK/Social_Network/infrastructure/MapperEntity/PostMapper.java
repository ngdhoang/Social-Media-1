package com.GHTK.Social_Network.infrastructure.MapperEntity;


import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.infrastructure.entity.post.ImagePostEntity;
import com.GHTK.Social_Network.infrastructure.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.entity.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.entity.post.TagUserEntity;
import com.GHTK.Social_Network.infrastructure.entity.post.comment.CommentEntity;
import com.GHTK.Social_Network.infrastructure.entity.user.UserEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface PostMapper {
  PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

  @Mapping(target = "userEntity", source = "userId")
  @Mapping(target = "imagePosts", ignore = true)
  @Mapping(target = "tagUserEntities", ignore = true)
  @Mapping(target = "reactionPosts", ignore = true)
  @Mapping(target = "comments", ignore = true)
  PostEntity toEntity(Post post);

  @Mapping(target = "userId", source = "userEntity.userId")
  @Mapping(target = "imagePostIds", expression = "java(mapImagePostIds(postEntity))")
  @Mapping(target = "tagUserIds", expression = "java(mapTagUserIds(postEntity))")
  @Mapping(target = "reactionPostIds", expression = "java(mapReactionPostIds(postEntity))")
  @Mapping(target = "commentIds", expression = "java(mapCommentIds(postEntity))")
  Post toDomain(PostEntity postEntity);

  @AfterMapping
  default void setUserId(@MappingTarget PostEntity postEntity, Post post) {
    if (post.getUserId() != null) {
      postEntity.setUserEntity(new UserEntity());
      postEntity.getUserEntity().setUserId(post.getUserId());
    }
  }

  default List<Long> mapImagePostIds(PostEntity postEntity) {
    return postEntity.getImagePosts() != null
            ? postEntity.getImagePosts().stream().map(ImagePostEntity::getImagePostId).collect(Collectors.toList())
            : null;
  }

  default List<Long> mapTagUserIds(PostEntity postEntity) {
    return postEntity.getTagUserEntities() != null
            ? postEntity.getTagUserEntities().stream().map(TagUserEntity::getTagUserId).collect(Collectors.toList())
            : null;
  }

  default List<Long> mapReactionPostIds(PostEntity postEntity) {
    return postEntity.getReactionPosts() != null
            ? postEntity.getReactionPosts().stream().map(ReactionPost::getReactionPostId).collect(Collectors.toList())
            : null;
  }

  default List<Long> mapCommentIds(PostEntity postEntity) {
    return postEntity.getComments() != null
            ? postEntity.getComments().stream().map(CommentEntity::getCommentId).collect(Collectors.toList())
            : null;
  }

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDomain(Post post, @MappingTarget PostEntity postEntity);
}