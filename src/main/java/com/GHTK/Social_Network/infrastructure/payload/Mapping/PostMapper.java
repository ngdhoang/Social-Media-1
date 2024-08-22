package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.post.EPostStatus;
import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.TagUser;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.PostBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.PostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostMapper {
  @Mapping(source = "post.postId", target = "postId")
  @Mapping(source = "post.content", target = "content")
  @Mapping(source = "post.createAt", target = "createAt")
  @Mapping(source = "post.updateAt", target = "updateAt")
  @Mapping(source = "post.postStatus", target = "status", qualifiedByName = "postStatusToString")
  @Mapping(source = "imagePosts", target = "imagePosts", qualifiedByName = "mapImagePosts")
  @Mapping(source = "tagUsers", target = "tagUsers", qualifiedByName = "mapTagUsers")
  @Mapping(source = "post.reactionsQuantity", target = "reactionsQuantity")
  @Mapping(source = "post.commentQuantity", target = "commentQuantity")
  PostResponse postToPostResponse(Post post, List<ImagePost> imagePosts, List<?> tagUsers, UserBasicDto user);

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
  @Mapping(source = "imagePostId", target = "imageId")
  List<ImageDto> mapImagePosts(List<ImagePost> imagePosts);

  @Named("mapTagUsers")
  default List<UserBasicDto> mapTagUsers(List<?> tagUsers) {
    if (tagUsers == null) {
      return null;
    }
    if (tagUsers.isEmpty()) {
      return List.of();
    }
    if (tagUsers.get(0) instanceof UserBasicDto) {
      return (List<UserBasicDto>) tagUsers;
    } else if (tagUsers.get(0) instanceof User) {
      return ((List<User>) tagUsers).stream()
              .map(this::userToUserBasicInfoDto)
              .collect(Collectors.toList());
    }
//    if (tagUsers.get(0) instanceof TagUser) {
//      return ((List<TagUser>) tagUsers).stream()
//              .map(tagUser -> UserBasicDto.builder()
//                      .userId(tagUser.getUserId())
//                      .firstName(tagUser.get())
//                      .build())
//              .collect(Collectors.toList());
//    }
    return List.of();
  }

  @Mapping(source = "imagePostId", target = "imageId")
  ImageDto imagePostToImageDto(ImagePost imagePost);

  @Mapping(source = "userId", target = "userId")
  UserBasicDto userToUserBasicInfoDto(User user);

  @Mapping(source = "postId", target = "postId")
  PostBasicDto postToPostBasicDto(Post post);
}