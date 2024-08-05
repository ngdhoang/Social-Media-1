package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.BlockPort;
import com.GHTK.Social_Network.common.customAnnotation.Enum.ESortBy;
import com.GHTK.Social_Network.domain.model.friendShip.FriendShip;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.FriendshipCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.friendShip.EFriendshipStatusEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.friendShip.FriendShipEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.FriendCollectionRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.FriendShipRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.mapper.EFriendShipStatusMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.FriendShipMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetBlockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BlockAdapter implements BlockPort {

  private final FriendShipRepository friendShipRepository;
  private final FriendCollectionRepository friendCollectionRepository;

  private final UserRepository userRepository;

  private final EFriendShipStatusMapperETD eFriendShipStatusMapperETD;
  private final FriendShipMapperETD friendShipMapperETD;

  @Override
  public List<FriendShip> getListBlock(GetBlockRequest getBlockRequest) {
    int page = getBlockRequest.getPage();
    int size = getBlockRequest.getSize();
    String orderBy = getBlockRequest.getOrderBy();
    String sortBy = getBlockRequest.getSortBy();
    Long userId = getBlockRequest.getUserId();
    sortBy = Objects.equals(sortBy, ESortBy.CREATED_AT.toString()) ? "createAt" : "friendShipId";
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(orderBy), sortBy));
    return friendShipRepository.getListBlock(userId, pageable).stream().map(friendShipMapperETD::toDomain).toList();
  }

  @Override
  public Boolean findUserById(Long id) {
    return userRepository.findById(id).isPresent();
  }

  @Override
  public FriendShip addBlock(Long userInitiatorId, Long userReceiveId) {
    FriendShipEntity friendShipEntity = new FriendShipEntity();
    friendShipEntity.setUserInitiatorId(userInitiatorId);
    friendShipEntity.setUserReceiveId(userReceiveId);
    friendShipEntity.setFriendshipStatus(EFriendshipStatusEntity.BLOCK);
    FriendShip friendShip = friendShipMapperETD.toDomain(friendShipRepository.save(friendShipEntity));
    FriendshipCollection friendshipCollection = friendCollectionRepository.findByUserId(userInitiatorId.toString());
    if (friendshipCollection == null) {
      friendshipCollection = new FriendshipCollection();
      friendshipCollection.setUserId(userInitiatorId.toString());
        LinkedList<Long> listBlockId = new LinkedList<>();
        listBlockId.add(userReceiveId);
        friendshipCollection.setListBlockId(listBlockId);
        friendCollectionRepository.save(friendshipCollection);
    } else {
        friendshipCollection.getListBlockId().add(userReceiveId);
        friendCollectionRepository.save(friendshipCollection);
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

    FriendshipCollection friendshipCollection = friendCollectionRepository.findByUserId(userReceiveId.toString());
    friendshipCollection.getListBlockId().remove(userInitiateId);
    friendCollectionRepository.save(friendshipCollection);
  }

  @Override
  public void unBlock(Long friendShipId) {
    friendShipRepository.deleteById(friendShipId);

    FriendShipEntity friendShipEntity = friendShipRepository.findBlockById(friendShipId);
    FriendshipCollection friendshipCollection = friendCollectionRepository.findByUserId(friendShipEntity.getUserReceiveId().toString());
    friendshipCollection.getListBlockId().remove(friendShipEntity.getUserInitiatorId());
    friendCollectionRepository.save(friendshipCollection);
  }

  @Override
  public Boolean isBlock(Long fistUserId, Long secondUserId) {
    return friendShipRepository.isBlock(fistUserId, secondUserId);
  }
}
