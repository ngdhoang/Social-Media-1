package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.domain.model.post.EPostStatus;
import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.UserBasicInfoDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.PostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostMapper {

  @Mapping(source = "post.postId", target = "postId")
  @Mapping(source = "post.content", target = "content")
  @Mapping(source = "post.createdAt", target = "createdAt")
  @Mapping(source = "post.updateAt", target = "updateAt")
  @Mapping(source = "post.postStatus", target = "status", qualifiedByName = "postStatusToString")
  @Mapping(source = "imagePosts", target = "imagePosts", qualifiedByName = "mapImagePosts")
  @Mapping(source = "tagUsers", target = "tagUsers", qualifiedByName = "mapTagUsers")
  @Mapping(target = "reactionsQuantity", ignore = true)
  @Mapping(target = "commentQuantity", ignore = true)
  PostResponse postToPostResponse(Post post, List<ImagePost> imagePosts, List<?> tagUsers);

  @Mapping(target = "postStatus", source = "status", qualifiedByName = "stringToPostStatus")
  @Mapping(target = "userId", ignore = true)
  Post postResponseToPost(PostResponse postResponse);

  @Named("postStatusToString")
  default String postStatusToString(EPostStatus postStatus) {
    return postStatus != null ? postStatus.name() : null;
  }

  @Named("stringToPostStatus")
  default EPostStatus stringToPostStatus(String status) {
    return status != null ? EPostStatus.valueOf(status) : null;
  }

  @Named("mapImagePosts")
  List<ImageDto> mapImagePosts(List<ImagePost> imagePosts);

  @Named("mapTagUsers")
  default List<UserBasicInfoDto> mapTagUsers(List<?> tagUsers) {
    if (tagUsers == null) {
      return null;
    }
    if (tagUsers.isEmpty()) {
      return List.of();
    }
    if (tagUsers.get(0) instanceof UserBasicInfoDto) {
      return (List<UserBasicInfoDto>) tagUsers;
    } else if (tagUsers.get(0) instanceof User) {
      return ((List<User>) tagUsers).stream()
              .map(this::userToUserBasicInfoDto)
              .collect(Collectors.toList());
    }
    throw new IllegalArgumentException("Unsupported tag users type");
  }

  @Mapping(source = "imagePostId", target = "imageId")
  ImageDto imagePostToImageDto(ImagePost imagePost);

  @Mapping(source = "userId", target = "userId")
  UserBasicInfoDto userToUserBasicInfoDto(User user);
}