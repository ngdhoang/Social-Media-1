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
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetFriendShipRequest;
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
    Long userId = getFriendShipRequest.getUserId();
    sortBy = Objects.equals(sortBy, ESortBy.CREATED_AT.toString()) ? "createAt" : "friendShipId";
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(orderBy), sortBy));

    if (getFriendShipRequest.getStatus().toUpperCase() == "REQUESTED") {
      return friendShipRepository.getListFriendRequest(userId, EFriendshipStatusEntity.PENDING, pageable).stream().map(friendShipMapperETD::toDomain).toList();
    }

    EFriendshipStatus status = getFriendShipRequest.getStatus() != null ? EFriendshipStatus.valueOf(getFriendShipRequest.getStatus().toUpperCase()) : null;
    EFriendshipStatusEntity statusEntity = status != null ? eFriendShipStatusMapperETD.toEntity(status) : null;

    if (status.equals(EFriendshipStatus.PENDING)) {
        List<FriendShipEntity> listFriendShipEntity = friendShipRepository.getListFriendReceiveRequest(userId, statusEntity, pageable);
        return listFriendShipEntity.stream().map(friendShipMapperETD::toDomain).toList();
    }
    if (status == null || !status.equals(EFriendshipStatus.BLOCK)) {
      List<FriendShipEntity> listFriendShipEntity = friendShipRepository.getListFriend(userId, statusEntity, pageable);
      return listFriendShipEntity.stream().map(friendShipMapperETD::toDomain).toList();
    }
    return friendShipRepository.getListBlock(userId, pageable).stream().map(friendShipMapperETD::toDomain).toList();
  }

  @Override
  public Long countByUserReceiveIdAndFriendshipStatus(GetFriendShipRequest getFriendShipRequest) {
    Long userId = getFriendShipRequest.getUserId();
    if (getFriendShipRequest.getStatus().toUpperCase() == "REQUESTED") {
      return friendShipRepository.countByUserRequestAndFriendshipStatus(userId, EFriendshipStatusEntity.PENDING);
    }
    EFriendshipStatus status = getFriendShipRequest.getStatus() != null ? EFriendshipStatus.valueOf(getFriendShipRequest.getStatus().toUpperCase()) : null;
    EFriendshipStatusEntity statusEntity = status != null ? eFriendShipStatusMapperETD.toEntity(status) : null;
    if (status.equals(EFriendshipStatus.PENDING) || status.equals(EFriendshipStatus.BLOCK)) {
      return friendShipRepository.countByUserReceiveIdAndFriendshipStatus(userId, statusEntity);
    }

    return friendShipRepository.countByUserIdAndFriendshipStatus(userId, statusEntity);
  }

  @Override
  public Long countByUserReceiveIdAndFriendshipStatus(Long userId, EFriendshipStatus status) {
    EFriendshipStatusEntity statusEntity = eFriendShipStatusMapperETD.toEntity(status);
    return friendShipRepository.countByUserReceiveIdAndFriendshipStatus(userId, statusEntity);
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
    friendShipEntity.setFriendshipStatus(eFriendShipStatusMapperETD.toEntity(status));
    FriendShip friendShip = friendShipMapperETD.toDomain(friendShipRepository.save(friendShipEntity));
    return friendShip;
  }

  @Override
  public Boolean setRequestFriendShip(Long friendShipId, EFriendshipStatus status) {
    FriendShipEntity friendShipEntity = friendShipRepository.findById(friendShipId).orElse(null);
    if (friendShipEntity == null) {
      return false;
    }
    friendShipEntity.setFriendshipStatus(eFriendShipStatusMapperETD.toEntity(status));
    friendShipRepository.save(friendShipEntity);
    return true;
  }

  @Override
  public FriendShip getFriendShip(Long userInitiatorId, Long userReceiveId) {
    FriendShipEntity friendShipEntity = friendShipRepository.findFriendShip(userInitiatorId, userReceiveId);
    return friendShipMapperETD.toDomain(friendShipEntity);
  }

  @Override
  public FriendShip getFriendShipById(Long id) {
    FriendShipEntity friendShipEntity= friendShipRepository.findById(id).orElse(null);
    return friendShipMapperETD.toDomain(friendShipEntity);
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
