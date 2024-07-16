package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.domain.entity.EFriendshipStatus;
import com.GHTK.Social_Network.domain.entity.FriendShip;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.FriendShipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendShipPortImpl implements FriendShipPort {

    private final FriendShipRepository friendShipRepository;


    @Override
    public Boolean addFriendShip(Long userInitiatorId, Long userReceiveId, EFriendshipStatus status) {
        FriendShip friendShip = new FriendShip();
        friendShip.setUserInitiatorId(userInitiatorId);
        friendShip.setUserReceiveId(userReceiveId);
        friendShip.setFriendshipStatus(status);
        friendShipRepository.save(friendShip);
        return true;
    }

    @Override
    public Boolean setRequestFriendShip(Long friendShipId, EFriendshipStatus status) {
        FriendShip friendShip = friendShipRepository.findById(friendShipId).orElse(null);
        if (friendShip == null) {
            return false;
        }
        friendShip.setFriendshipStatus(status);
        friendShipRepository.save(friendShip);
        return true;
    }

    @Override
    public FriendShip getFriendShip(Long userInitiatorId, Long userReceiveId) {
        FriendShip friendShip = friendShipRepository.findFriendShip(userInitiatorId, userReceiveId);
        return friendShip;
    }

    @Override
    public FriendShip getFriendShipById(Long id) {
        return friendShipRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteFriendShip(Long userReceiveId, Long userInitiateId) {
        FriendShip friendShip = friendShipRepository.findFriendShip(userReceiveId, userInitiateId);
        if (friendShip != null) {
            friendShipRepository.delete(friendShip);
        }
    }

    @Override
    public void deleteFriendShip(Long friendShipId) {
        friendShipRepository.deleteById(friendShipId);
    }

}
