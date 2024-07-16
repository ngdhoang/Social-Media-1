package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.requests.AcceptFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.SetRequestFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UnFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;

public interface FriendShipPortInput {
    MessageResponse setRequestFriendShip(SetRequestFriendRequest setRequestFriendRequest);

    MessageResponse acceptRequestFriendShip(AcceptFriendRequest acceptFriendRequest);

    void getFriendShip(Long firstUserId, Long secondUserId);

    MessageResponse unFriendShip(UnFriendShipRequest unFriendShipRequest);

}
