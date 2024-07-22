package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.infrastructure.entity.EFriendshipStatusEntity;
import com.GHTK.Social_Network.infrastructure.entity.FriendShipEntity;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetFriendShipRequest;

import java.util.List;

public interface FriendShipPort {
    Boolean addFriendShip(Long userInitiatorId, Long userReceiveId, EFriendshipStatusEntity status);

    Boolean setRequestFriendShip(Long friendShipId, EFriendshipStatusEntity status);

    FriendShipEntity getFriendShip(Long userInitiatorId, Long userReceiveId);

    FriendShipEntity getFriendShipById(Long id);

    void deleteFriendShip(Long userReceiveId, Long userInitiateId);

    void deleteFriendShip(Long friendShipId);

    List<FriendShipEntity> getListFriendShip(GetFriendShipRequest getFriendShipRequest);

    Boolean findUserById(Long id);

    Boolean isFriend(Long fistUserId, Long secondUserId);

    Boolean isBlock(Long fistUserId, Long secondUserId);

}