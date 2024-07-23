package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.ReactionPost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ReactionPostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ReactionPostMapperETD {

  @Mapping(source = "reactionPostId", target = "reactionPostId")
  ReactionPost toDomain(ReactionPostEntity reactionPostEntity);

  @Mapping(source = "reactionPostId", target = "reactionPostId")
  ReactionPostEntity toEntity(ReactionPost reactionPost);
}
