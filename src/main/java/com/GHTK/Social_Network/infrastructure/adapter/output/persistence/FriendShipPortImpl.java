package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.customAnnotation.Enum.ESortBy;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.domain.entity.EFriendshipStatus;
import com.GHTK.Social_Network.domain.entity.FriendShip;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.FriendShipRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetFriendShipRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendShipPortImpl implements FriendShipPort {

  private final FriendShipRepository friendShipRepository;

  private final UserRepository userRepository;

  @Override
  public List<FriendShip> getListFriendShip(GetFriendShipRequest getFriendShipRequest) {
    Integer page = getFriendShipRequest.getPage();
    Integer size = getFriendShipRequest.getSize();
    String orderBy = getFriendShipRequest.getOrderBy();
    String sortBy = getFriendShipRequest.getSortBy();
    EFriendshipStatus status = getFriendShipRequest.getStatus();
    Long userId = getFriendShipRequest.getUserId();
    sortBy = sortBy == ESortBy.CREATED_AT.toString() ? "createAt" : "friendShipId";
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(orderBy), sortBy));
    if (status == null || !status.equals(EFriendshipStatus.BLOCK.toString())) {
      List<FriendShip> listFriendShip = friendShipRepository.getListFriend(userId, status, pageable);
      return listFriendShip;
    }
    return friendShipRepository.getListBlock(userId, pageable);
  }

  @Override
  public Boolean findUserById(Long id) {
    return userRepository.findById(id).isPresent();
  }

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

  @Override
  public Boolean isFriend(Long fistUserId, Long secondUserId) {
    return friendShipRepository.isFriend(fistUserId, secondUserId);
  }

  @Override
  public Boolean isBlock(Long fistUserId, Long secondUserId) {
    return friendShipRepository.isBlock(fistUserId, secondUserId);
  }
}
