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
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.FriendShipRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.collection.UserCollectionRepository;
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
  private final UserCollectionRepository userCollectionRepository;
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
    return friendShip;
  }

  @Override
  public FriendShip setRequestFriendShip(Long friendShipId, EFriendshipStatus status) {
    FriendShipEntity friendShipEntity = friendShipRepository.findById(friendShipId).orElse(null);
    if (friendShipEntity == null) {
      return null;
    }
    friendShipEntity.setFriendshipStatus(eFriendShipStatusMapperETD.toEntity(status));
    FriendShipEntity friendShip = friendShipRepository.save(friendShipEntity);

    return friendShipMapperETD.toDomain(friendShip);
  }

  @Override
  public FriendShip getFriendShip(Long userInitiatorId, Long userReceiveId) {
    FriendShipEntity friendShipEntity = friendShipRepository.findFriendShip(userInitiatorId, userReceiveId);
    return friendShipMapperETD.toDomain(friendShipEntity);
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
    UserCollection userCollection = userCollectionRepository.findByUserId(fistUserId);
    return userCollection != null && (userCollection.getListBlockedId().contains(secondUserId) || userCollection.getListBlockId().contains(secondUserId));
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
    UserCollection userCollection = userCollectionRepository.findByUserId(userInitiatorId);
    UserCollection userCollectionReceive = userCollectionRepository.findByUserId(userReceiveId);
    if (userCollection == null || userCollectionReceive == null) {
      return 0;
    }
    List<Long> listFriendInitiator = userCollection.getListFriendId();
    List<Long> listFriendReceive = userCollectionReceive.getListFriendId();

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
  public LinkedList<Long> getListBlockBoth(Long userId) {
    LinkedList<Long> listBlock = new LinkedList<>();
    UserCollection userCollection = userCollectionRepository.findByUserId(userId);
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

  @Override
  public boolean isDeleteUser(Long userId) {
    return false;
//    return friendCollectionRepository.findByUserId(userId).isDelete();
  }
}
