package com.GHTK.Social_Network.infrastructure.payload.responses;

import com.GHTK.Social_Network.infrastructure.payload.dto.FriendShipUserDto;

import java.util.List;

public class FriendShipResponse {
    private Long userId;

    private List<FriendShipUserDto> users;

    private Long count;
}
