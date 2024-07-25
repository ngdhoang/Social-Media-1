package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.dto.FriendShipUserDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.AcceptFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.SetRequestFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UnFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;

import java.util.List;

public interface FriendShipPortInput {

  List<FriendShipUserDto> getListFriend(GetFriendShipRequest getFriendShipRequest);

  List<FriendShipUserDto> getListBlock(GetFriendShipRequest getFriendShipRequest);

  MessageResponse createRequestFriend(SetRequestFriendRequest setRequestFriendRequest);

  MessageResponse updateStateFriend(SetRequestFriendRequest setRequestFriendRequest);

  MessageResponse acceptFriendRequest(AcceptFriendRequest acceptFriendRequest);

  MessageResponse unFriendRequest(UnFriendShipRequest unFriendShipRequest);

  MessageResponse blockRequest(UnFriendShipRequest unFriendShipRequest);

  MessageResponse unBlockRequest(UnFriendShipRequest unFriendShipRequest);
}