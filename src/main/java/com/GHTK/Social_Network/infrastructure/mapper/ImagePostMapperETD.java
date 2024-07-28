package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.collection.ImageSequence;
import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.ImageSequenceCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ImagePostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Date;

@Mapper(componentModel = "spring")
public interface ImagePostMapperETD {
  @Mapping(source = "postEntity.postId", target = "postId")
  @Named("toDomainWithNullCheck")
  default ImagePost toDomain(ImagePostEntity imagePostEntity) {
    if (imagePostEntity == null) {
      return null;
    }
    return new ImagePost(
            imagePostEntity.getImagePostId(),
            imagePostEntity.getImageUrl(),
            imagePostEntity.getCreateAt(),
            imagePostEntity.getPostEntity() != null ? imagePostEntity.getPostEntity().getPostId() : null
    );
  }

  @Mapping(source = "postId", target = "postEntity")
  ImagePostEntity toEntity(ImagePost imagePost);

  ImageSequence toDomain(ImageSequenceCollection imageSequenceCollection);

  default PostEntity mapPostId(Long postId) {
    if (postId == null) {
      return null;
    }
    PostEntity postEntity = new PostEntity();
    postEntity.setPostId(postId);
    return postEntity;
  }

  @Mapping(source = "postId", target = "postEntity")
  ImagePostEntity toImagePostEntity(String imageUrl, Date createAt, Long postId);

  default ImagePost toImagePost(String imageUrl, Date createAt, Long postId) {
    return new ImagePost(imageUrl, createAt, postId);
  }
}