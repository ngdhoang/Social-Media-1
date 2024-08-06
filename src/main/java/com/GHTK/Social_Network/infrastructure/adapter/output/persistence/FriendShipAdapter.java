package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.domain.model.friendShip.EFriendshipStatus;
import com.GHTK.Social_Network.domain.model.friendShip.FriendShip;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.friendShip.EFriendshipStatusEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.friendShip.FriendShipEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.FriendCollectionRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.FriendShipRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.mapper.EFriendShipStatusMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.FriendShipMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetFriendShipRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FriendShipAdapter implements FriendShipPort {

  private final FriendShipRepository friendShipRepository;
  private final FriendCollectionRepository friendCollectionRepository;

  private final UserRepository userRepository;

  private final EFriendShipStatusMapperETD eFriendShipStatusMapperETD;
  private final FriendShipMapperETD friendShipMapperETD;

  @Override
  public List<FriendShip> getListFriendShip(GetFriendShipRequest getFriendShipRequest) {
    Pageable pageable = getFriendShipRequest.toPageable();
    Long userId = getFriendShipRequest.getUserId();
    String statusString = getFriendShipRequest.getStatus();
    if (statusString != null && statusString.equalsIgnoreCase("REQUESTED")) {
      return friendShipRepository.getListFriendRequest(userId, EFriendshipStatusEntity.PENDING, pageable).stream().map(friendShipMapperETD::toDomain).toList();
    }

    EFriendshipStatus status = statusString != null ? EFriendshipStatus.valueOf(statusString.toUpperCase()) : null;
    EFriendshipStatusEntity statusEntity = status != null ? eFriendShipStatusMapperETD.toEntity(status) : null;

    if (status != null && status.equals(EFriendshipStatus.PENDING)) {
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
    String statusString = getFriendShipRequest.getStatus();
    if (statusString != null && statusString.equalsIgnoreCase("REQUESTED")) {
      return friendShipRepository.countByUserRequestAndFriendshipStatus(userId, EFriendshipStatusEntity.PENDING);
    }
    EFriendshipStatus status = statusString != null ? EFriendshipStatus.valueOf(statusString.toUpperCase()) : null;
    EFriendshipStatusEntity statusEntity = status != null ? eFriendShipStatusMapperETD.toEntity(status) : null;
    if (status != null) {
      if (status.equals(EFriendshipStatus.PENDING)) {
        return friendShipRepository.countByUserReceiveIdAndFriendshipStatus(userId, statusEntity);
      }
      if (status.equals(EFriendshipStatus.BLOCK)) {
        return friendShipRepository.countByUserRequestAndFriendshipStatus(userId, statusEntity);
      }
    }

    return friendShipRepository.countByUserIdAndFriendshipStatus(userId, statusEntity);
  }

  @Override
  public Long countByUserReceiveIdAndFriendshipStatus(Long userId, EFriendshipStatus status) {
    EFriendshipStatusEntity statusEntity = eFriendShipStatusMapperETD.toEntity(status);
    return friendShipRepository.countByUserReceiveIdAndFriendshipStatus(userId, statusEntity);
  }

  @Override
  public Long countByUserInitiatorIdAndFriendshipStatus(GetFriendShipRequest getFriendShipRequest) {
    Long userId = getFriendShipRequest.getUserId();
    String statusString = getFriendShipRequest.getStatus();
    EFriendshipStatus status = statusString != null ? EFriendshipStatus.valueOf(statusString.toUpperCase()) : null;
    EFriendshipStatusEntity statusEntity = status != null ? eFriendShipStatusMapperETD.toEntity(status) : null;
    return friendShipRepository.countByUserRequestAndFriendshipStatus(userId, statusEntity);
  }


  @Override
  public Long countByUserInitiatorIdAndFriendshipStatus(Long userInitiatorId, EFriendshipStatus status) {
    EFriendshipStatusEntity statusEntity = eFriendShipStatusMapperETD.toEntity(status);
    return friendShipRepository.countByUserRequestAndFriendshipStatus(userInitiatorId, statusEntity);
  }

  @Override
  public Boolean findUserById(Long id) {
    return userRepository.findById(id).isPresent();
  }

  @Override
  public FriendShip addFriendShip(Long userInitiatorId, Long userReceiveId, EFriendshipStatus status) {
    FriendShipEntity friendShipEntity = new FriendShipEntity(userReceiveId, userInitiatorId, eFriendShipStatusMapperETD.toEntity(status));
    FriendShip friendShip = friendShipMapperETD.toDomain(friendShipRepository.save(friendShipEntity));

    UserCollection userCollection = friendCollectionRepository.findByUserId(userInitiatorId);
    UserCollection userCollectionReceive = friendCollectionRepository.findByUserId(userReceiveId);
    if (status != null && !status.equals(EFriendshipStatus.PENDING)) {
      if (userCollection != null) {
        userCollection.getListFriendId().add(userReceiveId);
        friendCollectionRepository.save(userCollection);
      } else {
        UserCollection newUserCollection = new UserCollection(userInitiatorId);
        newUserCollection.addFriend(userReceiveId);
        friendCollectionRepository.save(newUserCollection);
      }
      if (userCollectionReceive != null) {
        userCollectionReceive.getListFriendId().add(userInitiatorId);
        friendCollectionRepository.save(userCollectionReceive);
      } else {
        UserCollection newUserCollectionReceive = new UserCollection(userReceiveId);
        newUserCollectionReceive.addFriend(userInitiatorId);
        friendCollectionRepository.save(newUserCollectionReceive);
      }
    }
    return friendShip;
  }

  @Override
  public Boolean setRequestFriendShip(Long friendShipId, EFriendshipStatus status) {
    FriendShipEntity friendShipEntity = friendShipRepository.findById(friendShipId).orElse(null);
    if (friendShipEntity == null) {
      return false;
    }
    EFriendshipStatusEntity prevStatus = friendShipEntity.getFriendshipStatus();
    friendShipEntity.setFriendshipStatus(eFriendShipStatusMapperETD.toEntity(status));
    friendShipRepository.save(friendShipEntity);
    UserCollection userCollection = friendCollectionRepository.findByUserId(friendShipEntity.getUserInitiatorId());
    UserCollection userCollectionReceive = friendCollectionRepository.findByUserId(friendShipEntity.getUserReceiveId());
    if (status != null && status.equals(EFriendshipStatus.BLOCK)) {
      if (userCollection != null) {
        userCollection.getListBlockId().add(friendShipEntity.getUserReceiveId());
        userCollection.getListFriendId().remove(friendShipEntity.getUserReceiveId());
        friendCollectionRepository.save(userCollection);
      } else {
        UserCollection newUserCollection = new UserCollection(friendShipEntity.getUserInitiatorId());
        newUserCollection.addBlock(friendShipEntity.getUserReceiveId());
        friendCollectionRepository.save(newUserCollection);
      }
      if (userCollectionReceive != null) {
        userCollectionReceive.getListBlockedId().add(friendShipEntity.getUserInitiatorId());
        userCollectionReceive.getListFriendId().remove(friendShipEntity.getUserInitiatorId());
        friendCollectionRepository.save(userCollectionReceive);
      } else {
        UserCollection newUserCollectionReceive = new UserCollection(friendShipEntity.getUserReceiveId());
        newUserCollectionReceive.addBlocked(friendShipEntity.getUserInitiatorId());
        friendCollectionRepository.save(newUserCollectionReceive);
      }
    } else {
      if (userCollection != null) {
        if (prevStatus.equals((EFriendshipStatusEntity.PENDING))) {
          userCollection.getListFriendId().add(friendShipEntity.getUserReceiveId());
          friendCollectionRepository.save(userCollection);
        }
      } else {
        UserCollection newUserCollection = new UserCollection(friendShipEntity.getUserInitiatorId());
        newUserCollection.addFriend(friendShipEntity.getUserReceiveId());
        friendCollectionRepository.save(newUserCollection);
      }
      if (userCollectionReceive != null) {
        if (prevStatus.equals((EFriendshipStatusEntity.PENDING))) {
          userCollectionReceive.getListFriendId().add(friendShipEntity.getUserInitiatorId());
          friendCollectionRepository.save(userCollectionReceive);
        }
      } else {
        UserCollection newUserCollectionReceive = new UserCollection(friendShipEntity.getUserReceiveId());
        newUserCollectionReceive.addFriend(friendShipEntity.getUserInitiatorId());
        friendCollectionRepository.save(newUserCollectionReceive);
      }
    }
    return true;
  }

  @Override
  public FriendShip getFriendShip(Long userInitiatorId, Long userReceiveId) {
    FriendShipEntity friendShipEntity = friendShipRepository.findFriendShip(userInitiatorId, userReceiveId);
    return friendShipMapperETD.toDomain(friendShipEntity);
  }

  @Override
  public FriendShip getFriendShipById(Long id) {
    FriendShipEntity friendShipEntity = friendShipRepository.findById(id).orElse(null);
    return friendShipMapperETD.toDomain(friendShipEntity);
  }

  @Override
  public void deleteFriendShip(Long userReceiveId, Long userInitiateId) {
    FriendShipEntity friendShipEntity = friendShipRepository.findFriendShip(userReceiveId, userInitiateId);
    if (friendShipEntity != null) {
      friendShipRepository.delete(friendShipEntity);
      UserCollection userCollection = friendCollectionRepository.findByUserId(userInitiateId);
      UserCollection userCollectionReceive = friendCollectionRepository.findByUserId(userReceiveId);
      if (userCollection != null) {
        if (friendShipEntity.getFriendshipStatus().equals(EFriendshipStatusEntity.BLOCK)) {
          userCollection.getListBlockId().remove(userReceiveId);
        } else {
          userCollection.getListFriendId().remove(userReceiveId);
        }
        friendCollectionRepository.save(userCollection);
      }
      if (userCollectionReceive != null) {
        if (friendShipEntity.getFriendshipStatus().equals(EFriendshipStatusEntity.BLOCK)) {
          userCollectionReceive.getListBlockId().remove(userInitiateId);
        } else {
          userCollectionReceive.getListFriendId().remove(userInitiateId);
        }
        friendCollectionRepository.save(userCollectionReceive);
      }
    }
  }

  @Override
  public void deleteFriendShip(Long friendShipId) {
    FriendShipEntity friendShipEntity = friendShipRepository.findById(friendShipId).orElse(null);
    friendShipRepository.deleteById(friendShipId);
    if (friendShipEntity != null) {
      Long userInitiateId = friendShipEntity.getUserInitiatorId();
      Long userReceiveId = friendShipEntity.getUserReceiveId();
      UserCollection userCollection = friendCollectionRepository.findByUserId(userInitiateId);
      UserCollection userCollectionReceive = friendCollectionRepository.findByUserId(userReceiveId);
      if (userCollection != null) {
        if (friendShipEntity.getFriendshipStatus().equals(EFriendshipStatusEntity.BLOCK)) {
          userCollection.getListBlockId().remove(userReceiveId);
        } else {
          userCollection.getListFriendId().remove(userReceiveId);
        }
        friendCollectionRepository.save(userCollection);
      }

      if (userCollectionReceive != null) {
        if (friendShipEntity.getFriendshipStatus().equals(EFriendshipStatusEntity.BLOCK)) {
          userCollectionReceive.getListBlockId().remove(userInitiateId);
        } else {
          userCollectionReceive.getListFriendId().remove(userInitiateId);
        }
        friendCollectionRepository.save(userCollectionReceive);
      }
    }
  }

  @Override
  public Boolean isFriend(Long fistUserId, Long secondUserId) {
    return friendShipRepository.isFriend(fistUserId, secondUserId);
  }

  @Override
  public Boolean isBlock(Long fistUserId, Long secondUserId) {
    UserCollection userCollection = friendCollectionRepository.findByUserId(fistUserId);
    return userCollection != null && (userCollection.getListBlockedId().contains(secondUserId) || userCollection.getListBlockId().contains(secondUserId));
  }

  @Override
  public int getMutualFriend(Long userInitiatorId, Long userReceiveId) {
    UserCollection userCollection = friendCollectionRepository.findByUserId(userInitiatorId);
    UserCollection userCollectionReceive = friendCollectionRepository.findByUserId(userReceiveId);
    if (userCollection == null || userCollectionReceive == null) {
      return 0;
    }
    LinkedList<Long> listFriendInitiator = userCollection.getListFriendId();
    LinkedList<Long> listFriendReceive = userCollectionReceive.getListFriendId();
    Set<Long> multiFriend = Set.of(listFriendInitiator.toArray(new Long[0]));
    multiFriend.retainAll(listFriendReceive);
    return multiFriend.size();
  }

  @Override
  public LinkedList<Long> getListMeBlock(Long userId) {
    UserCollection userCollection = friendCollectionRepository.findByUserId(userId);
    if (userCollection == null) {
      return new LinkedList<>();
    }
    return userCollection.getListBlockId();
  }

  @Override
  public LinkedList<Long> getListBlockMe(Long userId) {
    LinkedList<Long> listBlock = new LinkedList<>();
    UserCollection userCollection = friendCollectionRepository.findByUserId(userId);
    if (userCollection != null && userCollection.getListBlockedId() != null) {
      listBlock.addAll(userCollection.getListBlockedId());
    }
    return listBlock;
  }

  @Override
  public LinkedList<Long> getListBlockBoth(Long userId) {
    LinkedList<Long> listBlock = new LinkedList<>();
    UserCollection userCollection = friendCollectionRepository.findByUserId(userId);
    if (userCollection == null) {
      return listBlock;
    }
    LinkedList<Long> listMeBlock = userCollection.getListBlockId();
    LinkedList<Long> listBlockMe = userCollection.getListBlockedId();
    if (listMeBlock != null) {
      listBlock.addAll(listMeBlock);
    }
    if (listBlockMe != null) {
      listBlock.addAll(listBlockMe);
    }

    return listBlock;
  }
}
