package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ImagePostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.TagUserEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.PostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper
public interface PostMapper {
  PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

  @Mapping(source = "imagePosts", target = "imagePosts", qualifiedByName = "imagePostsToUrls")
  @Mapping(source = "tagUsers", target = "tagUsers", qualifiedByName = "tagUserToTagUserDto")
  PostResponse postToPostResponse(Post post);

  @Named("imagePostsToUrls")
  default List<ImageDto> imagePostsToUrls(List<ImagePostEntity> imagePostEntities) {
    return imagePostEntities.stream()
            .map((imagePost) -> new ImageDto(imagePost.getImagePostId(), imagePost.getImageUrl(), imagePost.getCreateAt()))
            .collect(Collectors.toList());
  }

  @Named("tagUserToTagUserDto")
  default List<ProfileDto> tagUserToTagUserDto(List<TagUserEntity> tagUserEntityList) {
    return Optional.ofNullable(tagUserEntityList)
            .orElse(Collections.emptyList())
            .stream()
            .map(tu -> {
              UserEntity u = tu.getUserEntity();
              return ProfileDto.builder()
                      .profileId(u.getUserId())
                      .firstName(u.getFirstName())
                      .lastName(u.getLastName())
                      .userEmail(u.getUserEmail())
                      .avatar(u.getAvatar())
                      .build();
            })
            .collect(Collectors.toList());
  }

}
