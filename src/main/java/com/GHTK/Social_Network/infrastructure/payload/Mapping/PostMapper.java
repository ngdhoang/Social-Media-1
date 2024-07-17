package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.entity.Post;
import com.GHTK.Social_Network.infrastructure.payload.dto.PostDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(source = "postId", target = "postId")
    PostDto postToPostDto(Post post);

    @Mapping(source = "postId", target = "postId")
    Post postDtoToPost(PostDto post);
}
