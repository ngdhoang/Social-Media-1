package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.ReactionPost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ReactionPostEntity;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReactionPostMapper {
  ReactionResponse postToResponse(ReactionPost post);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updateAt", ignore = true)
  ReactionPost responseToPost(ReactionResponse response);
}