package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.BlockPortInput;
import com.GHTK.Social_Network.application.port.output.BlockPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.event.friendship.CreateBlockEvent;
import com.GHTK.Social_Network.domain.event.friendship.RemoveBlockEvent;
import com.GHTK.Social_Network.domain.event.friendship.UpdateFriendShipEvent;
import com.GHTK.Social_Network.domain.model.friendShip.EFriendshipStatus;
import com.GHTK.Social_Network.domain.model.friendShip.FriendShip;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.FriendShipMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.FriendShipUserDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetBlockRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.SetBlockRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.UnFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.FriendShipResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockService implements BlockPortInput {

  private User getUserAuth() {
    return authPort.getUserAuth();
  }

  private final AuthPort authPort;
  private final FriendShipPort friendShipPort;
  private final BlockPort blockPort;
  private final ProfilePort profilePort;
  private final ApplicationEventPublisher applicationEventPublisher;

  private final FriendShipMapper friendShipMapper;


  @Override
  public FriendShipResponse getListBlock(GetBlockRequest getBlockRequest) {
    User user = getUserAuth();
    if (getBlockRequest.getUserId() != null && !getBlockRequest.getUserId().equals(user.getUserId())) {
      throw new CustomException("Not permission", HttpStatus.FORBIDDEN);
    }
    getBlockRequest.setUserId(user.getUserId());
    List<FriendShip> friendShips = blockPort.getListBlock(getBlockRequest);
    List<FriendShipUserDto> friendShipUsersDto = getProfilesDto(user, friendShips);
    Long count = friendShipPort.countByUserInitiatorIdAndFriendshipStatus(getBlockRequest.getUserId(), EFriendshipStatus.BLOCK);
    return new FriendShipResponse(friendShipUsersDto, count);
  }

  private List<FriendShipUserDto> getProfilesDto(User user, List<FriendShip> friendShips) {
    Long mutualFriendsQuantity = 0L;
    List<User> profileUsers = friendShips.stream()
            .map(FriendShip::getUserReceiveId)
            .map(profilePort::takeUserById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    return friendShips.stream().map(
            f -> friendShipMapper.toFriendShipUserDto(
                    profileUsers.stream()
                            .filter(p -> p.getUserId().equals(f.getUserInitiatorId()) || p.getUserId().equals(f.getUserReceiveId()))
                            .findFirst()
                            .orElse(null), f.getFriendshipStatus(), mutualFriendsQuantity)).toList();
  }

  @Override
  public MessageResponse blockRequest(SetBlockRequest setBlockRequest) {
    User user = getUserAuth();
    Long userReceiveId = setBlockRequest.getUserId();

    if (user.getUserId().equals(userReceiveId)) {
      throw new CustomException("Cannot block request yourself", HttpStatus.BAD_REQUEST);
    }

    profilePort.takeUserById(userReceiveId).orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

    FriendShip friendShip = friendShipPort.getFriendShip(user.getUserId(), userReceiveId);
    FriendShip friendShipReverse = friendShipPort.getFriendShip(userReceiveId, user.getUserId());
    if(friendShipReverse != null && friendShip != null){
      throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }

    if (friendShip != null) {
      if (friendShip.getFriendshipStatus().equals(EFriendshipStatus.BLOCK)) {
        throw new CustomException("Duplicated request", HttpStatus.BAD_REQUEST);
      }
      friendShipPort.setRequestFriendShip(friendShip.getFriendShipId(), EFriendshipStatus.BLOCK);
      applicationEventPublisher.publishEvent(new UpdateFriendShipEvent(friendShip, EFriendshipStatus.BLOCK, friendShip.getFriendshipStatus()));
    } else if (friendShipReverse != null) {
      if (!friendShipReverse.getFriendshipStatus().equals(EFriendshipStatus.BLOCK)) {
        friendShipPort.deleteFriendShip(friendShipReverse.getFriendShipId());
      }
      blockPort.addBlock(user.getUserId(), userReceiveId);
      applicationEventPublisher.publishEvent(new CreateBlockEvent(user.getUserId(), userReceiveId));
    } else {
      blockPort.addBlock(user.getUserId(), userReceiveId);
      applicationEventPublisher.publishEvent(new CreateBlockEvent(user.getUserId(), userReceiveId));
    }
    return new MessageResponse("Request sent successfully");
  }

  @Override
  public MessageResponse unBlockRequest(UnFriendShipRequest unFriendShipRequest) {
    User user = getUserAuth();

    if (user.getUserId().equals(unFriendShipRequest.getUserId())) {
      throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }

    FriendShip friendShip = blockPort.getBlock(user.getUserId(), unFriendShipRequest.getUserId());

    if (friendShip == null) {
      throw new CustomException("Not found", HttpStatus.NOT_FOUND);
    }

    blockPort.unBlock(friendShip.getFriendShipId());

    applicationEventPublisher.publishEvent(new RemoveBlockEvent(friendShip));
    return new MessageResponse("Request sent successfully");
  }

}