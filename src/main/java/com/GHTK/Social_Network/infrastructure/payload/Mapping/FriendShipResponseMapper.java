package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.infrastructure.payload.dto.FriendShipUserDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.FriendShipResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FriendShipResponseMapper {
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "users", target = "users")
    @Mapping(source = "count", target = "count")
    FriendShipResponse toFriendShipResponse(Long userId, List<FriendShipUserDto> users, Long count);
}
