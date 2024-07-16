package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.FriendShipPortInput;
import com.GHTK.Social_Network.application.port.output.AuthPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.domain.entity.EFriendshipStatus;
import com.GHTK.Social_Network.domain.entity.FriendShip;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.exception.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.requests.AcceptFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.SetRequestFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UnFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendShipService implements FriendShipPortInput {

    private final Map<EFriendshipStatus, Integer> status = Map.of(
            EFriendshipStatus.PENDING, 0,
            EFriendshipStatus.CLOSE_FRIEND, 1,
            EFriendshipStatus.SIBLING, 2,
            EFriendshipStatus.PARENT, 3,
            EFriendshipStatus.BLOCK, 4,
            EFriendshipStatus.OTHER, 5
    );
    private final AuthPort authenticationRepositoryPort;

    private final FriendShipPort friendShipPort;

    private User getUserAuth() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }

        return authenticationRepositoryPort.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid token"));
    }

    @Override
    public MessageResponse setRequestFriendShip(SetRequestFriendRequest setRequestFriendRequest) {
        User user = getUserAuth();
        Long userReceiveId = setRequestFriendRequest.getUserReceiveId();
        EFriendshipStatus requestedStatus = getKey(status, setRequestFriendRequest.getStatus());

        if (user.getUserId().equals(userReceiveId)) {
            throw new CustomException("Invalid request: Cannot send request to yourself", HttpStatus.BAD_REQUEST);
        }

        FriendShip friendShip = friendShipPort.getFriendShip(user.getUserId(), userReceiveId);
        FriendShip friendShipReverse = friendShipPort.getFriendShip(userReceiveId, user.getUserId());

        if (friendShip == null || friendShipReverse == null) {
            if (friendShip == null && friendShipReverse == null) {
                if (setRequestFriendRequest.getStatus() == status.get(EFriendshipStatus.BLOCK)) {
                    friendShipPort.addFriendShip(user.getUserId(), setRequestFriendRequest.getUserReceiveId(), EFriendshipStatus.BLOCK);
                    return new MessageResponse("Request successfully");
                } else if (setRequestFriendRequest.getStatus() == status.get(EFriendshipStatus.CLOSE_FRIEND)) {
                    friendShipPort.addFriendShip(user.getUserId(), setRequestFriendRequest.getUserReceiveId(), EFriendshipStatus.PENDING);
                    return new MessageResponse("Request successfully");
                }
                throw new CustomException("Request is invalid", HttpStatus.BAD_REQUEST);

            } else if (friendShip == null && friendShipReverse != null) {
                if (friendShipReverse.getFriendshipStatus().equals(requestedStatus) || (friendShipReverse.getFriendshipStatus().equals(EFriendshipStatus.PENDING) && requestedStatus.equals(EFriendshipStatus.CLOSE_FRIEND))) {
                    throw new CustomException("Request is duplicated", HttpStatus.BAD_REQUEST);
                }
                if (friendShipReverse.getFriendshipStatus().equals(EFriendshipStatus.PENDING) && setRequestFriendRequest.getStatus() != status.get(EFriendshipStatus.CLOSE_FRIEND) && setRequestFriendRequest.getStatus() != status.get(EFriendshipStatus.BLOCK)) {
                    throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
                }
                if (friendShipReverse.getFriendshipStatus().equals(EFriendshipStatus.BLOCK) && setRequestFriendRequest.getStatus() == status.get(EFriendshipStatus.BLOCK)) {
                    friendShipPort.addFriendShip(user.getUserId(), setRequestFriendRequest.getUserReceiveId(), EFriendshipStatus.BLOCK);
                    return new MessageResponse("Request successfully");
                } else if (friendShipReverse.getFriendshipStatus().equals(EFriendshipStatus.BLOCK) && setRequestFriendRequest.getStatus() != status.get(EFriendshipStatus.BLOCK)) {
                    throw new CustomException("User was blocked", HttpStatus.FORBIDDEN);
                }
            } else if (friendShip != null && friendShipReverse == null) {
                if (friendShip.getFriendshipStatus().equals(requestedStatus) || (friendShip.getFriendshipStatus().equals(EFriendshipStatus.PENDING) && requestedStatus.equals(EFriendshipStatus.CLOSE_FRIEND))) {
                    throw new CustomException("Request is duplicated", HttpStatus.BAD_REQUEST);
                }
                if (friendShip.getFriendshipStatus().equals(EFriendshipStatus.PENDING) && setRequestFriendRequest.getStatus() != status.get(EFriendshipStatus.CLOSE_FRIEND) && setRequestFriendRequest.getStatus() != status.get(EFriendshipStatus.BLOCK)) {
                    throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
                }
                if (friendShip.getFriendshipStatus().equals(EFriendshipStatus.BLOCK) && setRequestFriendRequest.getStatus() != status.get(EFriendshipStatus.BLOCK)) {
                    throw new CustomException("This user was blocked", HttpStatus.FORBIDDEN);
                }
                friendShipPort.addFriendShip(user.getUserId(), setRequestFriendRequest.getUserReceiveId(), requestedStatus);
                friendShipPort.deleteFriendShip(friendShip.getFriendShipId());
                return new MessageResponse("Request sent successfully");
            }
        } else {
            if (friendShip.getFriendshipStatus().equals(requestedStatus) || (friendShip.getFriendshipStatus().equals(EFriendshipStatus.PENDING) && requestedStatus.equals(EFriendshipStatus.CLOSE_FRIEND))) {
                throw new CustomException("Request is duplicated", HttpStatus.BAD_REQUEST);
            }
            if (friendShip.getFriendshipStatus().equals(EFriendshipStatus.PENDING) && setRequestFriendRequest.getStatus() != status.get(EFriendshipStatus.CLOSE_FRIEND) && setRequestFriendRequest.getStatus() != status.get(EFriendshipStatus.BLOCK)) {
                throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
            }
            if (!friendShip.getFriendshipStatus().equals(EFriendshipStatus.BLOCK)) {
                friendShipPort.setRequestFriendShip(friendShip.getFriendShipId(), requestedStatus);
                return new MessageResponse("Request successfully");
            } else if (friendShip.getUserReceiveId().equals(user.getUserId()) && setRequestFriendRequest.getStatus() == status.get(EFriendshipStatus.BLOCK)) {
                if (friendShipReverse != null) {
                    friendShipPort.addFriendShip(user.getUserId(), setRequestFriendRequest.getUserReceiveId(), EFriendshipStatus.BLOCK);
                    return new MessageResponse("Request successfully");
                }
                throw new CustomException("User was blocked", HttpStatus.FORBIDDEN);
            }
            friendShipPort.setRequestFriendShip(friendShip.getFriendShipId(), requestedStatus);
        }
        return new MessageResponse("Request sent successfully");
    }

    @Override
    public MessageResponse acceptRequestFriendShip(AcceptFriendRequest acceptFriendRequest) {
        User user = getUserAuth();

        FriendShip friendShip = friendShipPort.getFriendShipById(acceptFriendRequest.getFriendId());

        if (friendShip == null || !friendShip.getFriendshipStatus().equals(EFriendshipStatus.PENDING)) {
            throw new CustomException("Friendship not found", HttpStatus.NOT_FOUND);
        }
        if (acceptFriendRequest.getIsAccept() == 1) {
            friendShipPort.setRequestFriendShip(acceptFriendRequest.getFriendId(), EFriendshipStatus.CLOSE_FRIEND);
            return new MessageResponse("Request sent successfully");
        } else if (acceptFriendRequest.getIsAccept() == 0) {
            friendShipPort.deleteFriendShip(acceptFriendRequest.getFriendId());
            return new MessageResponse("Request sent successfully");
        }
        throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }


    @Override
    public void getFriendShip(Long firstUserId, Long secondUserId) {

    }

    @Override
    public MessageResponse unFriendShip(UnFriendShipRequest unFriendShipRequest) {
        User user = getUserAuth();
        if (user.getUserId().equals(unFriendShipRequest.getFriendId())) {
            throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
        }

        FriendShip friendShip = friendShipPort.getFriendShip(user.getUserId(), unFriendShipRequest.getFriendId());

        if (friendShip == null || (!friendShip.getUserReceiveId().equals(user.getUserId()) && !friendShip.getUserInitiatorId().equals(user.getUserId()))) {
            throw new CustomException("Friendship not found", HttpStatus.NOT_FOUND);
        }
        friendShipPort.deleteFriendShip(friendShip.getFriendShipId());
        return new MessageResponse("Request sent successfully");
    }

    private EFriendshipStatus getKey(Map<EFriendshipStatus, Integer> map, Integer value) {
        for (Map.Entry<EFriendshipStatus, Integer> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
