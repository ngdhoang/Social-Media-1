package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.model.friendShip.EFriendshipStatus;
import com.GHTK.Social_Network.domain.model.friendShip.FriendShip;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetFriendShipRequest;

import java.util.LinkedList;
import java.util.List;

public interface FriendShipPort {
    FriendShip addFriendShip(Long userInitiatorId, Long userReceiveId, EFriendshipStatus status);

    FriendShip setRequestFriendShip(Long friendShipId, EFriendshipStatus status);

    FriendShip getFriendShip(Long userInitiatorId, Long userReceiveId);

    void deleteFriendShip(Long friendShipId);

    List<FriendShip> getListFriendShip(GetFriendShipRequest getFriendShipRequest);

    Long countByUserReceiveIdAndFriendshipStatus(GetFriendShipRequest getFriendShipRequest);


    Long countByUserInitiatorIdAndFriendshipStatus(Long userInitiatorId, EFriendshipStatus status);

    Boolean isFriend(Long fistUserId, Long secondUserId);

    Boolean isBlock(Long fistUserId, Long secondUserId);

    List<Long> getListSuggestFriend(Long userId);

    int getMutualFriend(Long userInitiatorId, Long userReceiveId);


    int getMutualFriendNeo(Long firstUser, Long secondUser);

    LinkedList<Long> getListBlockBoth(Long userId);

    boolean isDeleteUser(Long userId);

}