package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.model.FriendShip;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetBlockRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetFriendShipRequest;

import java.util.List;

public interface BlockPort {
    FriendShip addBlock(Long userInitiatorId, Long userReceiveId);

    FriendShip getBlock(Long userInitiatorId, Long userReceiveId);

    FriendShip getBlockById(Long id);

    FriendShip findBlock(Long userInitiatorId, Long userReceiveId);

    FriendShip findBlockById(Long id);

    void unBlock(Long userReceiveId, Long userInitiateId);

    void unBlock(Long friendShipId);

    List<FriendShip> getListBlock(GetBlockRequest getBlockRequest);

    Boolean findUserById(Long id);

    Boolean isBlock(Long fistUserId, Long secondUserId);
}