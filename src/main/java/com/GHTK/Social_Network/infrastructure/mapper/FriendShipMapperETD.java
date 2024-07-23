package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.FriendShip;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.FriendShipEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendShipMapperETD {
  @Mapping(source = "friendShipId", target = "friendShipId")
  FriendShip toDomain(FriendShipEntity friendShipEntity);

  @Mapping(source = "friendShipId", target = "friendShipId")
  FriendShipEntity toEntity(FriendShip friendShip);
}
