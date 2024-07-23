package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.EFriendshipStatus;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.EFriendshipStatusEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EFriendShipStatusMapperETD {
  EFriendshipStatus toDomain(EFriendshipStatusEntity eFriendshipStatusEntity) ;

  EFriendshipStatusEntity toEntity(EFriendshipStatus eFriendshipStatus) ;
}
