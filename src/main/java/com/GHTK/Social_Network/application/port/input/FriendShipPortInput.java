package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.dto.FriendShipUserDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.AcceptFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.SetRequestFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.UnFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.FriendShipResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;

import java.util.List;

public interface FriendShipPortInput {

  FriendShipResponse getListFriend(GetFriendShipRequest getFriendShipRequest);

  MessageResponse createRequestFriend(SetRequestFriendRequest setRequestFriendRequest);

  MessageResponse updateStateFriend(SetRequestFriendRequest setRequestFriendRequest);

  MessageResponse acceptFriendRequest(AcceptFriendRequest acceptFriendRequest);

  MessageResponse unFriendRequest(UnFriendShipRequest unFriendShipRequest);

}