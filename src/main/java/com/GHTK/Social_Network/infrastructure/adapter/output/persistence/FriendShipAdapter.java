package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.common.customAnnotation.Enum.ESortBy;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.domain.model.EFriendshipStatus;
import com.GHTK.Social_Network.domain.model.FriendShip;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.EFriendshipStatusEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.FriendShipEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.FriendShipRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.mapper.EFriendShipStatusMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.FriendShipMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetFriendShipRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FriendShipAdapter implements FriendShipPort {

  private final FriendShipRepository friendShipRepository;

  private final UserRepository userRepository;

  private final EFriendShipStatusMapperETD eFriendShipStatusMapperETD;
  private final FriendShipMapperETD friendShipMapperETD;

  @Override
  public List<FriendShip> getListFriendShip(GetFriendShipRequest getFriendShipRequest) {
    int page = getFriendShipRequest.getPage();
    int size = getFriendShipRequest.getSize();
    String orderBy = getFriendShipRequest.getOrderBy();
    String sortBy = getFriendShipRequest.getSortBy();
    EFriendshipStatus status = getFriendShipRequest.getStatus();
    EFriendshipStatusEntity statusEntity = eFriendShipStatusMapperETD.toEntity(status);
    Long userId = getFriendShipRequest.getUserId();
    sortBy = Objects.equals(sortBy, ESortBy.CREATED_AT.toString()) ? "createAt" : "friendShipId";
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(orderBy), sortBy));
    if (statusEntity == null || !status.equals(EFriendshipStatus.BLOCK)) {
      List<FriendShipEntity> listFriendShipEntity = friendShipRepository.getListFriend(userId, statusEntity, pageable);
      return listFriendShipEntity.stream().map(friendShipMapperETD::toDomain).toList();
    }
    return friendShipRepository.getListBlock(userId, pageable).stream().map(friendShipMapperETD::toDomain).toList();
  }

  @Override
  public Boolean findUserById(Long id) {
    return userRepository.findById(id).isPresent();
  }

  @Override
  public FriendShip addFriendShip(Long userInitiatorId, Long userReceiveId, EFriendshipStatus status) {
    FriendShipEntity friendShipEntity = new FriendShipEntity();
    friendShipEntity.setUserInitiatorId(userInitiatorId);
    friendShipEntity.setUserReceiveId(userReceiveId);
    friendShipEntity.setFriendshipStatus(status);
    friendShipRepository.save(friendShipEntity);
    return true;
  }

  @Override
  public Boolean setRequestFriendShip(Long friendShipId, EFriendshipStatusEntity status) {
    FriendShipEntity friendShipEntity = friendShipRepository.findById(friendShipId).orElse(null);
    if (friendShipEntity == null) {
      return false;
    }
    friendShipEntity.setFriendshipStatus(status);
    friendShipRepository.save(friendShipEntity);
    return true;
  }

  @Override
  public FriendShipEntity getFriendShip(Long userInitiatorId, Long userReceiveId) {
    FriendShipEntity friendShipEntity = friendShipRepository.findFriendShip(userInitiatorId, userReceiveId);
    return friendShipEntity;
  }

  @Override
  public FriendShipEntity getFriendShipById(Long id) {
    return friendShipRepository.findById(id).orElse(null);
  }

  @Override
  public void deleteFriendShip(Long userReceiveId, Long userInitiateId) {
    FriendShipEntity friendShipEntity = friendShipRepository.findFriendShip(userReceiveId, userInitiateId);
    if (friendShipEntity != null) {
      friendShipRepository.delete(friendShipEntity);
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
