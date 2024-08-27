package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReactionInfoMapper {
  @Mapping(source = "user", target = "user")
  @Mapping(source = "reactionType", target = "type")
  ReactionUserDto toReactionInfoResponse(User user, EReactionType reactionType);

}