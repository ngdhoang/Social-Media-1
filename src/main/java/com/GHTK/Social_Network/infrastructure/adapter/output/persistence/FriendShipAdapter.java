package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.domain.model.friendShip.EFriendshipStatus;
import com.GHTK.Social_Network.domain.model.friendShip.FriendShip;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.friendShip.EFriendshipStatusEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.friendShip.FriendShipEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.FriendSuggestion;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.RelationshipScores;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.UserNode;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.FriendCollectionRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.FriendShipRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.node.UserNodeRepository;
import com.GHTK.Social_Network.infrastructure.mapper.EFriendShipStatusMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.FriendShipMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetFriendShipRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FriendShipAdapter implements FriendShipPort {

  private final FriendShipRepository friendShipRepository;
  private final FriendCollectionRepository friendCollectionRepository;
  private final UserRepository userRepository;
  private final UserNodeRepository userNodeRepository;

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


    UserCollection friendshipCollection = friendCollectionRepository.findByUserId(userInitiatorId);
    UserCollection friendshipCollectionReceive = friendCollectionRepository.findByUserId(userReceiveId);
    if (status != null && !status.equals(EFriendshipStatus.PENDING)) {
      if (friendshipCollection != null) {
        friendshipCollection.getListFriendId().add(userReceiveId);
        friendCollectionRepository.save(friendshipCollection);
      } else {
        UserCollection newUserCollection = new UserCollection(userInitiatorId);
        newUserCollection.addFriend(userReceiveId);
        friendCollectionRepository.save(newUserCollection);
      }
      if (friendshipCollectionReceive != null) {
        friendshipCollectionReceive.getListFriendId().add(userInitiatorId);
        friendCollectionRepository.save(friendshipCollectionReceive);
      } else {
        UserCollection newUserCollectionReceive = new UserCollection(userReceiveId);
        newUserCollectionReceive.addFriend(userInitiatorId);
        friendCollectionRepository.save(newUserCollectionReceive);
      }
      userNodeRepository.createFriend(userInitiatorId, userReceiveId, eFriendShipStatusMapperETD.toEntity(status));
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
    UserCollection friendshipCollection = friendCollectionRepository.findByUserId(friendShipEntity.getUserInitiatorId());
    UserCollection friendshipCollectionReceive = friendCollectionRepository.findByUserId(friendShipEntity.getUserReceiveId());
    if (status != null && status.equals(EFriendshipStatus.BLOCK)) {
      if (friendshipCollection != null) {
        friendshipCollection.getListBlockId().add(friendShipEntity.getUserReceiveId());
        friendshipCollection.getListFriendId().remove(friendShipEntity.getUserReceiveId());
        friendCollectionRepository.save(friendshipCollection);
        System.out.println("block1");
      } else {
        UserCollection newUserCollection = new UserCollection(friendShipEntity.getUserInitiatorId());
        newUserCollection.addBlock(friendShipEntity.getUserReceiveId());
        friendCollectionRepository.save(newUserCollection);
        System.out.println("block2");
      }

      if (friendshipCollectionReceive != null) {
        friendshipCollectionReceive.getListBlockedId().add(friendShipEntity.getUserInitiatorId());
        friendshipCollectionReceive.getListFriendId().remove(friendShipEntity.getUserInitiatorId());
        friendCollectionRepository.save(friendshipCollectionReceive);
        System.out.println("block3");
      } else {
        UserCollection newUserCollectionReceive = new UserCollection(friendShipEntity.getUserReceiveId());
        newUserCollectionReceive.addBlocked(friendShipEntity.getUserInitiatorId());
        friendCollectionRepository.save(newUserCollectionReceive);
        System.out.println("block4");
      }

      userNodeRepository.createBlockUser(friendShipEntity.getUserInitiatorId(), friendShipEntity.getUserReceiveId());

    } else {
      if (friendshipCollection != null) {
        if (prevStatus.equals((EFriendshipStatusEntity.PENDING))) {
          friendshipCollection.getListFriendId().add(friendShipEntity.getUserReceiveId());
          friendCollectionRepository.save(friendshipCollection);

          System.out.println("add1");
        }
      } else {
        UserCollection newUserCollection = new UserCollection(friendShipEntity.getUserInitiatorId());
        newUserCollection.addFriend(friendShipEntity.getUserReceiveId());
        friendCollectionRepository.save(newUserCollection);

        System.out.println("add2");
      }
      if (friendshipCollectionReceive != null) {
        if (prevStatus.equals((EFriendshipStatusEntity.PENDING))) {
          friendshipCollectionReceive.getListFriendId().add(friendShipEntity.getUserInitiatorId());
          friendCollectionRepository.save(friendshipCollectionReceive);

          System.out.println("add3");
        }
      } else {
        UserCollection newUserCollectionReceive = new UserCollection(friendShipEntity.getUserReceiveId());
        newUserCollectionReceive.addFriend(friendShipEntity.getUserInitiatorId());
        friendCollectionRepository.save(newUserCollectionReceive);

        System.out.println("add4");
      }

      System.out.println("add5");
      System.out.println("type:" + status.getClass());
      System.out.println(eFriendShipStatusMapperETD.toEntity(status));
      EFriendshipStatusEntity statusEntity = status == null ? EFriendshipStatusEntity.CLOSE_FRIEND : eFriendShipStatusMapperETD.toEntity(status);
      userNodeRepository.createOrUpdateFriend(friendShipEntity.getUserInitiatorId(), friendShipEntity.getUserReceiveId(), statusEntity);

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
      EFriendshipStatusEntity status = friendShipEntity.getFriendshipStatus();

      friendShipRepository.delete(friendShipEntity);
      UserCollection friendshipCollection = friendCollectionRepository.findByUserId(userInitiateId);
      UserCollection friendshipCollectionReceive = friendCollectionRepository.findByUserId(userReceiveId);
      if (friendshipCollection != null) {
        if (friendShipEntity.getFriendshipStatus().equals(EFriendshipStatusEntity.BLOCK)) {
          friendshipCollection.getListBlockId().remove(userReceiveId);
        } else {
          friendshipCollection.getListFriendId().remove(userReceiveId);
        }
        friendCollectionRepository.save(friendshipCollection);
      }
      if (friendshipCollectionReceive != null) {
        if (friendShipEntity.getFriendshipStatus().equals(EFriendshipStatusEntity.BLOCK)) {
          friendshipCollectionReceive.getListBlockId().remove(userInitiateId);
        } else {
          friendshipCollectionReceive.getListFriendId().remove(userInitiateId);
        }
        friendCollectionRepository.save(friendshipCollectionReceive);
      }

      if (status != null && status.equals(EFriendshipStatusEntity.BLOCK)) {
        userNodeRepository.unblockUser(userReceiveId, userInitiateId);
      } else {
        userNodeRepository.deleteFriend(userReceiveId, userInitiateId);
      }
    }
  }

  @Override
  public void deleteFriendShip(Long friendShipId) {
    FriendShipEntity friendShipEntity = friendShipRepository.findById(friendShipId).orElse(null);
    friendShipRepository.deleteById(friendShipId);
    if (friendShipEntity != null) {
      EFriendshipStatusEntity status = friendShipEntity.getFriendshipStatus();
      Long userInitiateId = friendShipEntity.getUserInitiatorId();
      Long userReceiveId = friendShipEntity.getUserReceiveId();
      UserCollection friendshipCollection = friendCollectionRepository.findByUserId(userInitiateId);
      UserCollection friendshipCollectionReceive = friendCollectionRepository.findByUserId(userReceiveId);
      if (friendshipCollection != null) {
        if (friendShipEntity.getFriendshipStatus().equals(EFriendshipStatusEntity.BLOCK)) {
          friendshipCollection.getListBlockId().remove(userReceiveId);
        } else {
          friendshipCollection.getListFriendId().remove(userReceiveId);
        }
        friendCollectionRepository.save(friendshipCollection);
      }

      if (friendshipCollectionReceive != null) {
        if (friendShipEntity.getFriendshipStatus().equals(EFriendshipStatusEntity.BLOCK)) {
          friendshipCollectionReceive.getListBlockId().remove(userInitiateId);
        } else {
          friendshipCollectionReceive.getListFriendId().remove(userInitiateId);
        }
        friendCollectionRepository.save(friendshipCollectionReceive);
      }

      if (status != null && status.equals(EFriendshipStatusEntity.BLOCK)) {
        userNodeRepository.unblockUser(userReceiveId, userInitiateId);
      } else {
        userNodeRepository.deleteFriend(userReceiveId, userInitiateId);
      }
    }
  }

  @Override
  public Boolean isFriend(Long fistUserId, Long secondUserId) {
    return friendShipRepository.isFriend(fistUserId, secondUserId);
  }

  @Override
  public Boolean isBlock(Long fistUserId, Long secondUserId) {
    UserCollection friendshipCollection = friendCollectionRepository.findByUserId(fistUserId);
    return friendshipCollection != null && (friendshipCollection.getListBlockedId().contains(secondUserId) || friendshipCollection.getListBlockId().contains(secondUserId));
  }

  @Override
  public List<Long> getListSuggestFriend(Long userId) {
    List<FriendSuggestion> listFriend = userNodeRepository.getListPotentialFriends(
            userId,
            RelationshipScores.CLOSE_FRIEND_SCORE,
            RelationshipScores.SIBLING_SCORE,
            RelationshipScores.PARENT_SCORE,
            RelationshipScores.OTHER_SCORE,
            RelationshipScores.SAME_HOMETOWN_SCORE
    );

    return listFriend.stream()
            .map(FriendSuggestion::getPotentialFriend)
            .map(UserNode::getUserId)
            .toList();
  }

  @Override
  public int getMutualFriend(Long userInitiatorId, Long userReceiveId) {
    UserCollection friendshipCollection = friendCollectionRepository.findByUserId(userInitiatorId);
    UserCollection friendshipCollectionReceive = friendCollectionRepository.findByUserId(userReceiveId);
    if (friendshipCollection == null || friendshipCollectionReceive == null) {
      return 0;
    }
    List<Long> listFriendInitiator = friendshipCollection.getListFriendId();
    List<Long> listFriendReceive = friendshipCollectionReceive.getListFriendId();

    if (listFriendInitiator == null || listFriendReceive == null) {
      return 0;
    }

    Set<Long> multiFriend = new HashSet<>(listFriendInitiator);
    multiFriend.retainAll(listFriendReceive);
    return multiFriend.size();
  }

  @Override
  public int getMutualFriendNeo(Long firstUser, Long secondUser) {
    return userNodeRepository.getMutualFriend(firstUser, secondUser);
  }

  @Override
  public LinkedList<Long> getListMeBlock(Long userId) {
    UserCollection friendshipCollection = friendCollectionRepository.findByUserId(userId);
    if (friendshipCollection == null) {
      return new LinkedList<>();
    }
    return friendshipCollection.getListBlockId();
  }

  @Override
  public LinkedList<Long> getListBlockMe(Long userId) {
    LinkedList<Long> listBlock = new LinkedList<>();
    UserCollection friendshipCollection = friendCollectionRepository.findByUserId(userId);
    if (friendshipCollection != null && friendshipCollection.getListBlockedId() != null) {
      listBlock.addAll(friendshipCollection.getListBlockedId());
    }
    return listBlock;
  }

  @Override
  public LinkedList<Long> getListBlockBoth(Long userId) {
    LinkedList<Long> listBlock = new LinkedList<>();
    UserCollection friendshipCollection = friendCollectionRepository.findByUserId(userId);
    if (friendshipCollection == null) {
      return listBlock;
    }
    LinkedList<Long> listMeBlock = friendshipCollection.getListBlockId();
    LinkedList<Long> listBlockMe = friendshipCollection.getListBlockedId();
    if (listMeBlock != null) {
      listBlock.addAll(listMeBlock);
    }
    if (listBlockMe != null) {
      listBlock.addAll(listBlockMe);
    }

    return listBlock;
  }

  @Override
  public boolean isDeleteUser(Long userId) {
    return false;
//    return friendCollectionRepository.findByUserId(userId).isDelete();
  }
}
