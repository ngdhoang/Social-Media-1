package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.requests.SetRequestFriendRequest;

public interface FriendShipPortInput {
    void setRequestFriendShip(SetRequestFriendRequest setRequestFriendRequest);

    void acceptRequestFriendShip(Long id);

    void getFriendShip(Long firstUserId, Long secondUserId);

}
