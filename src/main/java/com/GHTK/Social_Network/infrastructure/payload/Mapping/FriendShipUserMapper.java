package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.EFriendshipStatus;
import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.FriendShipUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendShipUserMapper {
    @Mapping(source = "user.userId", target = "profileId")
    @Mapping(source = "friendshipStatus", target = "friendshipStatus")
    FriendShipUserDto toFriendShipUserDto(User user, EFriendshipStatus friendshipStatus);


}
