package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.entity.EFriendshipStatus;
import com.GHTK.Social_Network.domain.entity.FriendShip;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetFriendShipRequest;

import java.util.List;

public interface FriendShipPort {
  Boolean addFriendShip(Long userInitiatorId, Long userReceiveId, EFriendshipStatus status);

  Boolean setRequestFriendShip(Long friendShipId, EFriendshipStatus status);

  FriendShip getFriendShip(Long userInitiatorId, Long userReceiveId);

  FriendShip getFriendShipById(Long id);

  void deleteFriendShip(Long userReceiveId, Long userInitiateId);

  void deleteFriendShip(Long friendShipId);

  List<FriendShip> getListFriendShip(GetFriendShipRequest getFriendShipRequest);

  Boolean findUserById(Long id);

  Boolean isFriend(Long fistUserId, Long secondUserId);

}
