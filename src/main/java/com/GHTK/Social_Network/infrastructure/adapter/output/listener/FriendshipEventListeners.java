package com.GHTK.Social_Network.infrastructure.adapter.output.listener;

import com.GHTK.Social_Network.domain.event.friendship.CreatFriendEvent;
import com.GHTK.Social_Network.domain.event.friendship.CreateBlockEvent;
import com.GHTK.Social_Network.domain.event.friendship.RemoveBlockEvent;
import com.GHTK.Social_Network.domain.event.friendship.UpdateFriendShipEvent;
import com.GHTK.Social_Network.domain.model.friendShip.EFriendshipStatus;
import com.GHTK.Social_Network.domain.model.friendShip.FriendShip;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.friendShip.EFriendshipStatusEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.collection.UserCollectionRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.node.UserNodeRepository;
import com.GHTK.Social_Network.infrastructure.mapper.EFriendShipStatusMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendshipEventListeners {
    private final UserCollectionRepository userCollectionRepository;
    private final EFriendShipStatusMapperETD eFriendShipStatusMapperETD;
    private final UserNodeRepository userNodeRepository;

    @EventListener
    public void handleBlockEvent(CreateBlockEvent event) {
        Long userInitiatorId = event.getUserInitiatorId();
        Long userReceiveId = event.getUserReceiverId();

        processBlockUserCollection(userInitiatorId, userReceiveId, true);
        processBlockUserCollection(userReceiveId, userInitiatorId, false);
        createBlockUser(userInitiatorId, userReceiveId);
    }

    @EventListener
    public void handleUnBlockEvent(RemoveBlockEvent event) {
        FriendShip friendShip = event.getFriendShip();

        UserCollection userCollection = getUserCollection(friendShip.getUserInitiatorId());
        UserCollection userCollectionReceive = getUserCollection(friendShip.getUserReceiveId());

        handlerUnBlockCollection(userCollection, userCollectionReceive);

        deleteBlockUser(friendShip.getUserInitiatorId(), friendShip.getUserReceiveId());
    }

    @EventListener
    public void handleAcceptFriendEvent(CreatFriendEvent event) {
        FriendShip friendShip = event.getFriendShip();
        Long userInitiatorId = friendShip.getUserInitiatorId();
        Long userReceiveId = friendShip.getUserReceiveId();
        EFriendshipStatus status = friendShip.getFriendshipStatus();

        processFriendship(userInitiatorId, userReceiveId, status);
    }

    @EventListener
    public void handleUpdateFriendEvent(UpdateFriendShipEvent event) {
        FriendShip friendShip = event.getFriendship();
        EFriendshipStatus status = event.getStatus();
        EFriendshipStatus prevStatus = event.getPreviousStatus();

        Long userInitiatorId = friendShip.getUserInitiatorId();
        Long userReceiveId = friendShip.getUserReceiveId();

        if (status != null && status.equals(EFriendshipStatus.BLOCK)) {
            handleBlockStatus(userInitiatorId, userReceiveId);
            deleteFriend(userInitiatorId, userReceiveId);
        } else {
            handleFriendStatus(userInitiatorId, userReceiveId, prevStatus, status);
        }
    }

    @EventListener
    public void handleRemoveFriendEvent(RemoveBlockEvent event) {
        FriendShip friendShip = event.getFriendShip();
        if (friendShip != null) {
            processFriendship(friendShip);
        }
    }

    private void handleBlockStatus(Long userInitiatorId, Long userReceiveId) {
        processBlockUserCollection(userInitiatorId, userReceiveId, true);
        processBlockUserCollection(userReceiveId, userInitiatorId, false);
        createBlockUser(userInitiatorId, userReceiveId);
    }

    private void handleFriendStatus(Long userInitiatorId, Long userReceiveId, EFriendshipStatus prevStatus, EFriendshipStatus status) {
        boolean wasPending = prevStatus != null && prevStatus.equals(EFriendshipStatus.PENDING);

        if (wasPending) {
            addFriendIfPending(userInitiatorId, userReceiveId, status);
        }

        EFriendshipStatusEntity statusEntity = (status == null) ? EFriendshipStatusEntity.CLOSE_FRIEND : eFriendShipStatusMapperETD.toEntity(status);
        userNodeRepository.createOrUpdateFriend(userInitiatorId, userReceiveId, statusEntity);
    }

    private void addFriendIfPending(Long userInitiatorId, Long userReceiveId, EFriendshipStatus status) {
        processFriendship(userInitiatorId, userReceiveId, status);
        processFriendship(userReceiveId, userInitiatorId, status);
    }

    private void processFriendship(FriendShip friendShipEntity) {
        Long userInitiateId = friendShipEntity.getUserInitiatorId();
        Long userReceiveId = friendShipEntity.getUserReceiveId();
        EFriendshipStatus status = friendShipEntity.getFriendshipStatus();

        UserCollection userCollection = getUserCollection(userInitiateId);
        UserCollection userCollectionReceive = getUserCollection(userReceiveId);

        if (userCollection != null) {
            updateUserCollection(userCollection, userReceiveId, status);
        }

        if (userCollectionReceive != null) {
            updateUserCollection(userCollectionReceive, userInitiateId, status);
        }

        handleUserNodeStatus(status, userReceiveId, userInitiateId);
    }

    private void updateUserCollection(UserCollection userCollection, Long userId, EFriendshipStatus status) {
        if (status.equals(EFriendshipStatus.BLOCK)) {
            userCollection.getListBlockId().remove(userId);
        } else {
            userCollection.getListFriendId().remove(userId);
        }
        saveUserCollection(userCollection);
    }

    private void handleUserNodeStatus(EFriendshipStatus status, Long userReceiveId, Long userInitiateId) {
        if (status != null && status.equals(EFriendshipStatus.BLOCK)) {
            userNodeRepository.unblockUser(userReceiveId, userInitiateId);
        } else {
            deleteFriend(userReceiveId, userInitiateId);
        }
    }

    private void processFriendship(Long userInitiatorId, Long userReceiveId, EFriendshipStatus status) {
        processFriendUserCollection(userInitiatorId, userReceiveId);
        processFriendUserCollection(userReceiveId, userInitiatorId);
        createFriend(userInitiatorId, userReceiveId, status);
    }

    private void processFriendUserCollection(Long userId, Long friendId) {
        UserCollection userCollection = getUserCollection(userId);
        if (userCollection == null) {
            userCollection = new UserCollection(userId);
            userCollection.addFriend(friendId);
        } else {
            userCollection.getListFriendId().add(friendId);
        }
        saveUserCollection(userCollection);
    }

    private void processBlockUserCollection(Long userId, Long targetId, boolean isInitiator) {
        UserCollection userCollection = getUserCollection(userId);
        if (userCollection == null) {
            userCollection = new UserCollection(userId);
        }
        if (isInitiator) {
            userCollection.addBlock(targetId);
            userCollection.getListFriendId().remove(targetId);
        } else {
            userCollection.addBlocked(targetId);
            userCollection.getListFriendId().remove(targetId);
        }
        saveUserCollection(userCollection);
    }

    private void handlerUnBlockCollection(UserCollection userCollection, UserCollection userCollectionReceive) {
        userCollection.getListBlockId().remove(userCollectionReceive.getUserId());
        saveUserCollection(userCollection);
        userCollectionReceive.getListBlockedId().remove(userCollection.getUserId());
        saveUserCollection(userCollectionReceive);
    }

    private void createFriend(Long userInitiatorId, Long userReceiveId, EFriendshipStatus status) {
        userNodeRepository.createOrUpdateFriend(userInitiatorId, userReceiveId, eFriendShipStatusMapperETD.toEntity(status));
    }

    private UserCollection getUserCollection(Long userId) {
        return userCollectionRepository.findByUserId(userId);
    }

    private void createBlockUser(Long userInitiatorId, Long userReceiveId) {
        userNodeRepository.createBlockUser(userInitiatorId, userReceiveId);
    }

    private void deleteBlockUser(Long userInitiatorId, Long userReceiveId) {
        userNodeRepository.unblockUser(userInitiatorId, userReceiveId);
    }

    private void deleteFriend(Long userInitiatorId, Long userReceiveId) {
        userNodeRepository.deleteFriend(userInitiatorId, userReceiveId);
    }

    private void saveUserCollection(UserCollection userCollection) {
        userCollectionRepository.save(userCollection);
    }
}
