package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.collection.ImageSequenceDomain;
import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.ImageSequence;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ImagePostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Date;

@Mapper(componentModel = "spring")
public interface ImagePostMapperETD {
  @Mapping(source = "postEntity.postId", target = "postId")
  ImagePost toDomain(ImagePostEntity imagePostEntity);

  @Mapping(source = "postId", target = "postEntity")
  ImagePostEntity toEntity(ImagePost imagePost);

  ImageSequenceDomain toDomain(ImageSequence imageSequence);

  default PostEntity mapPostId(Long postId) {
    if (postId == null) {
      return null;
    }
    PostEntity postEntity = new PostEntity();
    postEntity.setPostId(postId);
    return postEntity;
  }

  // Additional methods to handle the constructors
  @Mapping(source = "postId", target = "postEntity")
  ImagePostEntity toImagePostEntity(String imageUrl, Date createAt, Long postId);

  default ImagePost toImagePost(String imageUrl, Date createAt, Long postId) {
    return new ImagePost(imageUrl, createAt, postId);
  }
}