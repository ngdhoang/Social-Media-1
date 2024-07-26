package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.BlockPortInput;
import com.GHTK.Social_Network.application.port.output.BlockPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.EFriendshipStatus;
import com.GHTK.Social_Network.domain.model.FriendShip;
import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.FriendShipResponseMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.FriendShipUserMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.UserMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.FriendShipUserDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.*;
import com.GHTK.Social_Network.infrastructure.payload.responses.FriendShipResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockService implements BlockPortInput {

    private User getUserAuth() {
        User user = authPort.getUserAuth();
        return user == null ? User.builder().userId(0L).build() : user;
    }

    private final AuthPort authPort;
    private final FriendShipPort friendShipPort;
    private final BlockPort blockPort;
    private final ProfilePort profilePort;

    private final UserMapper userMapper;
    private final FriendShipUserMapper friendShipUserMapper;
    private final FriendShipResponseMapper friendShipResponseMapper;


    @Override
    public FriendShipResponse getListBlock(GetBlockRequest getBlockRequest) {
        User user = getUserAuth();
        if (getBlockRequest.getUserId() != null && !getBlockRequest.getUserId().equals(user.getUserId())) {
            throw new CustomException("Not permission", HttpStatus.FORBIDDEN);
        }
        getBlockRequest.setUserId(user.getUserId());
        List<FriendShip> friendShips = blockPort.getListBlock(getBlockRequest);
        List<FriendShipUserDto> friendShipUserDtos = getProfileDtos(user, friendShips);
        Long count = friendShipPort.countByUserReceiveIdAndFriendshipStatus(getBlockRequest.getUserId(), EFriendshipStatus.BLOCK);
        return friendShipResponseMapper.toFriendShipResponse(user.getUserId(), friendShipUserDtos, count);
    }

    private List<FriendShipUserDto> getProfileDtos(User user, List<FriendShip> friendShips) {
        Long mutualFriendsQuantity = 0L;
        List<User> profileUsers = friendShips.stream()
                .map(friendShip -> friendShip.getUserReceiveId())
                .map(profilePort::takeProfileById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        List<FriendShipUserDto> friendShipUserDtos = friendShips.stream()
                .map(friendShip -> friendShipUserMapper.toFriendShipUserDto(profileUsers.stream()
                        .filter(profileUser -> profileUser.getUserId().equals(friendShip.getUserInitiatorId()) || profileUser.getUserId().equals(friendShip.getUserReceiveId()))
                        .findFirst()
                        .orElse(null), friendShip.getFriendshipStatus(), mutualFriendsQuantity))
                .toList();

        return friendShipUserDtos;
    }

    @Override
    public MessageResponse blockRequest(SetBlockRequest setBlockRequest) {
        User user = getUserAuth();
        Long userReceiveId = setBlockRequest.getUserId();

        if (user.getUserId().equals(userReceiveId)) {
            throw new CustomException("Cannot block request yourself", HttpStatus.BAD_REQUEST);
        }

        FriendShip friendShip = friendShipPort.getFriendShip(user.getUserId(), userReceiveId);
        if (friendShip != null) {
            if (friendShip.getFriendshipStatus().equals(EFriendshipStatus.BLOCK)) {
                throw new CustomException("Duplicated request", HttpStatus.BAD_REQUEST);
            }
            friendShipPort.setRequestFriendShip(friendShip.getFriendShipId(), EFriendshipStatus.BLOCK);

            FriendShip friendShipReverse = friendShipPort.getFriendShip(userReceiveId, user.getUserId());
            if (friendShipReverse != null && !friendShipReverse.getFriendshipStatus().equals(EFriendshipStatus.BLOCK)) {
                friendShipPort.deleteFriendShip(friendShipReverse.getFriendShipId());
            }
            return new MessageResponse("Request sent successfully");
        }

        friendShip = friendShipPort.getFriendShip(userReceiveId, user.getUserId());
        if (friendShip != null) {
            if (friendShip.getFriendshipStatus().equals(EFriendshipStatus.BLOCK)) {
                blockPort.addBlock(user.getUserId(), userReceiveId);
                return new MessageResponse("Request sent successfully");
            }
            friendShipPort.deleteFriendShip(friendShip.getFriendShipId());
            return new MessageResponse("Request sent successfully");
        }

        blockPort.addBlock(user.getUserId(), userReceiveId);
        return new MessageResponse("Request sent successfully");
    }

    @Override
    public MessageResponse unBlockRequest(UnFriendShipRequest unFriendShipRequest) {
        User user = getUserAuth();

        if (user.getUserId().equals(unFriendShipRequest.getUserId())) {
            throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
        }

        FriendShip friendShip = blockPort.getBlock(user.getUserId(), unFriendShipRequest.getUserId());

        if (friendShip != null) {
            throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
        }

        friendShipPort.deleteFriendShip(friendShip.getFriendShipId());
        return new MessageResponse("Request sent successfully");
    }

}