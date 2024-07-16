package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.entity.EFriendshipStatus;
import com.GHTK.Social_Network.domain.entity.FriendShip;

public interface FriendShipPort {
    Boolean addFriendShip(Long userInitiatorId, Long userReceiveId, EFriendshipStatus status);

    Boolean setRequestFriendShip(Long friendShipId, EFriendshipStatus status);

    FriendShip getFriendShip(Long userInitiatorId, Long userReceiveId);

    FriendShip getFriendShipById(Long id);

    void deleteFriendShip(Long userReceiveId, Long userInitiateId);

    void deleteFriendShip(Long friendShipId);

}
