package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.model.friendShip.FriendShip;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetBlockRequest;

import java.util.List;

public interface BlockPort {
    FriendShip addBlock(Long userInitiatorId, Long userReceiveId);

    FriendShip getBlock(Long userInitiatorId, Long userReceiveId);

    void unBlock(Long friendShipId);

    List<FriendShip> getListBlock(GetBlockRequest getBlockRequest);

    Boolean isBlock(Long fistUserId, Long secondUserId);
}