package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.TagUser;
import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.PostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {
  PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

  @Mapping(source = "post.postId", target = "postId")
  @Mapping(source = "post.content", target = "content")
  @Mapping(source = "post.createdAt", target = "createdAt")
  @Mapping(source = "post.updateAt", target = "updateAt")
  @Mapping(source = "imagePosts", target = "imagePosts", qualifiedByName = "mapImagePosts")
  @Mapping(source = "tagUsers", target = "tagUsers", qualifiedByName = "mapTagUsers")
  PostResponse postToPostResponse(Post post, List<ImagePost> imagePosts, List<TagUser> tagUsers);

  @Named("mapImagePosts")
  List<ImageDto> mapImagePosts(List<ImagePost> imagePosts);

  @Named("mapTagUsers")
  List<ProfileDto> mapTagUsers(List<TagUser> tagUsers);

  @Mapping(source = "imagePostId", target = "id")
  ImageDto imagePostToImageDto(ImagePost imagePost);

  @Mapping(source = "user.userId", target = "profileId")
  ProfileDto userToProfileDto(User user);
}

  @Mapper(componentModel = "spring")
  interface ImageMapper {
    ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);

    @Mapping(source = "imagePostId", target = "id")
    ImageDto imagePostToImageDto(ImagePost imagePost);

    @Mapping(source = "id", target = "imagePostId")
    ImagePost imageDtoToImagePost(ImageDto imageDto);
  }

  @Mapper(componentModel = "spring")
  interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "userId", target = "profileId")
    ProfileDto userToProfileDto(User user);

    @Mapping(source = "profileId", target = "userId")
    User profileDtoToUser(ProfileDto profileDto);
}