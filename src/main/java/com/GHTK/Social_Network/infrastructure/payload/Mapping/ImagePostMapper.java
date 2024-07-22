package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.infrastructure.entity.post.ImagePostEntity;
import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ImagePostMapper {
  ImagePostMapper INSTANCE = Mappers.getMapper(ImagePostMapper.class);

  @Mapping(source = "imagePostId", target = "id")
  ImageDto imagePostToImageDto(ImagePostEntity imagePost);
}
