package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.BlockPort;
import com.GHTK.Social_Network.domain.model.friendShip.FriendShip;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.friendShip.EFriendshipStatusEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.friendShip.FriendShipEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.FriendCollectionRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.FriendShipRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.mapper.FriendShipMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetBlockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockAdapter implements BlockPort {

  private final FriendShipRepository friendShipRepository;
  private final FriendCollectionRepository friendCollectionRepository;

  private final UserRepository userRepository;
  private final FriendShipMapperETD friendShipMapperETD;

  @Override
  public List<FriendShip> getListBlock(GetBlockRequest getBlockRequest) {
    Pageable pageable = getBlockRequest.toPageable();
    Long userId = getBlockRequest.getUserId();
    return friendShipRepository.getListBlock(userId, pageable).stream().map(friendShipMapperETD::toDomain).toList();
  }

  @Override
  public Boolean findUserById(Long id) {
    return userRepository.findById(id).isPresent();
  }

  @Override
  public FriendShip addBlock(Long userInitiatorId, Long userReceiveId) {
    FriendShipEntity friendShipEntity = new FriendShipEntity(userReceiveId, userInitiatorId, EFriendshipStatusEntity.BLOCK);
    FriendShip friendShip = friendShipMapperETD.toDomain(friendShipRepository.save(friendShipEntity));
    UserCollection userCollection = friendCollectionRepository.findByUserId(userInitiatorId);
    UserCollection userCollectionReceive = friendCollectionRepository.findByUserId(userReceiveId);
    if (userCollection == null) {
        UserCollection newUserCollection = new UserCollection(userInitiatorId);
        newUserCollection.addBlock(userReceiveId);
        friendCollectionRepository.save(newUserCollection);
    } else {
        userCollection.getListBlockId().add(userReceiveId);
        friendCollectionRepository.save(userCollection);
    }
    if (userCollectionReceive == null) {
        UserCollection newUserCollectionReceive = new UserCollection(userReceiveId);
        newUserCollectionReceive.addBlocked(userInitiatorId);
        friendCollectionRepository.save(newUserCollectionReceive);
    } else {
        userCollectionReceive.getListBlockedId().add(userInitiatorId);
        friendCollectionRepository.save(userCollectionReceive);
    }
    return friendShip;
  }

  @Override
  public FriendShip getBlock(Long userInitiatorId, Long userReceiveId) {
    FriendShipEntity friendShipEntity = friendShipRepository.findBlock(userInitiatorId, userReceiveId);
    return friendShipMapperETD.toDomain(friendShipEntity);
  }

  @Override
  public FriendShip getBlockById(Long id) {
    FriendShipEntity friendShipEntity= friendShipRepository.findBlockById(id);
    return friendShipMapperETD.toDomain(friendShipEntity);
  }

  @Override
  public FriendShip findBlock(Long userInitiatorId, Long userReceiveId) {
    FriendShipEntity friendShipEntity = friendShipRepository.findBlock(userInitiatorId, userReceiveId);
    return friendShipMapperETD.toDomain(friendShipEntity);
  }

  @Override
  public FriendShip findBlockById(Long id) {
    return null;
  }

  @Override
  public void unBlock(Long userReceiveId, Long userInitiateId) {
    FriendShipEntity friendShipEntity = friendShipRepository.findBlock(userReceiveId, userInitiateId);
    if (friendShipEntity != null) {
      friendShipRepository.delete(friendShipEntity);
    }
    UserCollection userCollection = friendCollectionRepository.findByUserId(userInitiateId);
    UserCollection userCollectionReceive = friendCollectionRepository.findByUserId(userReceiveId);
    handlerUnBlockCollection(userCollection, userCollectionReceive);
  }

  @Override
  public void unBlock(Long friendShipId) {
    FriendShipEntity friendShipEntity = friendShipRepository.findBlockById(friendShipId);
    friendShipRepository.deleteById(friendShipId);
    UserCollection userCollection = friendCollectionRepository.findByUserId(friendShipEntity.getUserInitiatorId());
    UserCollection userCollectionReceive = friendCollectionRepository.findByUserId(friendShipEntity.getUserReceiveId());
    handlerUnBlockCollection(userCollection, userCollectionReceive);
  }

  private void handlerUnBlockCollection(UserCollection userCollection, UserCollection userCollectionReceive) {
    userCollection.getListBlockId().remove(userCollectionReceive.getUserId());
    friendCollectionRepository.save(userCollection);
    userCollectionReceive.getListBlockedId().remove(userCollection.getUserId());
    friendCollectionRepository.save(userCollectionReceive);
  }

  @Override
  public Boolean isBlock(Long fistUserId, Long secondUserId) {
    return friendShipRepository.isBlock(fistUserId, secondUserId);
  }
}
