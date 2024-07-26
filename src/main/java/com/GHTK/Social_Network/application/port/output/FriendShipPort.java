package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.model.EFriendshipStatus;
import com.GHTK.Social_Network.domain.model.FriendShip;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetFriendShipRequest;

import java.util.List;

public interface FriendShipPort {
    FriendShip addFriendShip(Long userInitiatorId, Long userReceiveId, EFriendshipStatus status);

    Boolean setRequestFriendShip(Long friendShipId, EFriendshipStatus status);

    FriendShip getFriendShip(Long userInitiatorId, Long userReceiveId);

    FriendShip getFriendShipById(Long id);

    void deleteFriendShip(Long userReceiveId, Long userInitiateId);

    void deleteFriendShip(Long friendShipId);

    List<FriendShip> getListFriendShip(GetFriendShipRequest getFriendShipRequest);

    Long countByUserReceiveIdAndFriendshipStatus(GetFriendShipRequest getFriendShipRequest);

    Long countByUserReceiveIdAndFriendshipStatus(Long userReceiveId, EFriendshipStatus status);

    Boolean findUserById(Long id);

    Boolean isFriend(Long fistUserId, Long secondUserId);

    Boolean isBlock(Long fistUserId, Long secondUserId);
}