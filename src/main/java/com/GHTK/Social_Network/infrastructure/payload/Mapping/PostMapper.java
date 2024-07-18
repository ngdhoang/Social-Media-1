package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.entity.post.ImagePost;
import com.GHTK.Social_Network.domain.entity.post.Post;
import com.GHTK.Social_Network.domain.entity.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.PostResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface PostMapper {
  PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

  @Mapping(source = "imagePosts", target = "imagePost", qualifiedByName = "imagePostsToUrls")
  PostResponse postToPostResponse(Post post);

  @Mapping(source = "reactionPostId", target = "reactionPostId")
  ReactionPostResponse reactionPostToReactionPostResponse(ReactionPost reactionPost);

  @Named("imagePostsToUrls")
  default List<String> imagePostsToUrls(List<ImagePost> imagePosts) {
    return imagePosts.stream()
            .map(ImagePost::getImageUrl)  // Giả sử ImagePost có phương thức getUrl()
            .collect(Collectors.toList());
  }
}
