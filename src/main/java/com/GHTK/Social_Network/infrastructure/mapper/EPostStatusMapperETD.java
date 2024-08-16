package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.friendShip.EFriendshipStatus;
import com.GHTK.Social_Network.domain.model.post.EPostStatus;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.friendShip.EFriendshipStatusEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EPostStatusMapperETD {
  EPostStatus toDomain(EPostStatusEntity ePostStatusEntity) ;

  EPostStatusEntity toEntity(EPostStatus ePostStatus) ;
}
