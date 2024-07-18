package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.AcceptFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.SetRequestFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UnFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;

import java.util.List;

public interface FriendShipPortInput {
    List<ProfileDto> getFriendShip(GetFriendShipRequest getFriendShipRequest);

    MessageResponse setRequestFriendShip(SetRequestFriendRequest setRequestFriendRequest);

    MessageResponse acceptRequestFriendShip(AcceptFriendRequest acceptFriendRequest);

    MessageResponse unFriendShip(UnFriendShipRequest unFriendShipRequest);

}
