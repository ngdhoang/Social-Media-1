package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.dto.FriendShipUserDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetBlockRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.SetBlockRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.UnFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.FriendShipResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface BlockPortInput {
    FriendShipResponse getListBlock(GetBlockRequest getBlockRequest);

    MessageResponse blockRequest(SetBlockRequest setBlockRequest);

    MessageResponse unBlockRequest(UnFriendShipRequest unFriendShipRequest);
}