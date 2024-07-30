package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.FriendShipPortInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.friendShip.EFriendshipStatus;
import com.GHTK.Social_Network.domain.model.friendShip.FriendShip;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.FriendShipMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.FriendShipUserDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.AcceptFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.SetRequestFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.UnFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.FriendShipResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendShipService implements FriendShipPortInput {

  private User getUserAuth() {
    return authPort.getUserAuth();
  }

  private final AuthPort authPort;
  private final FriendShipPort friendShipPort;
  private final ProfilePort profilePort;
  private final FriendShipMapper friendShipMapper;


  @Override
  public FriendShipResponse getListFriend(GetFriendShipRequest getFriendShipRequest) {
    User user = getUserAuth();
    if (getFriendShipRequest.getUserId() == null || getFriendShipRequest.getUserId().equals(user.getUserId())) {
      getFriendShipRequest.setUserId(user.getUserId());
      List<FriendShip> friendShips = friendShipPort.getListFriendShip(getFriendShipRequest);
      List<FriendShipUserDto> friendShipUsersDto = getProfileDtos(user, friendShips);
      Long count = friendShipPort.countByUserReceiveIdAndFriendshipStatus(getFriendShipRequest);
      return new FriendShipResponse(friendShipUsersDto, count);
    }

    User userTarget = profilePort.takeUserById(getFriendShipRequest.getUserId())
            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

    if (!userTarget.getIsProfilePublic() || (getFriendShipRequest.getStatus() != null && getFriendShipRequest.getStatus().equals(EFriendshipStatus.PENDING.toString()))) {
      throw new CustomException("Not permission", HttpStatus.FORBIDDEN);
    }

    FriendShip friendShip = friendShipPort.getFriendShip(user.getUserId(), userTarget.getUserId());
    FriendShip friendShipReverse = friendShipPort.getFriendShip(userTarget.getUserId(), user.getUserId());


    if ((friendShip != null && friendShip.getFriendshipStatus().equals(EFriendshipStatus.BLOCK)) || (friendShipReverse != null && friendShipReverse.getFriendshipStatus().equals(EFriendshipStatus.BLOCK))) {
      throw new CustomException("Not permission", HttpStatus.FORBIDDEN);
    }

    List<FriendShip> friendShips = friendShipPort.getListFriendShip(getFriendShipRequest);
    List<FriendShipUserDto> friendShipUsersDto = getProfileDtos(userTarget, friendShips);
    Long count = friendShipPort.countByUserReceiveIdAndFriendshipStatus(getFriendShipRequest);
    return new FriendShipResponse(friendShipUsersDto, count);
  }

  private List<FriendShipUserDto> getProfileDtos(User user, List<FriendShip> friendShips) {
    List<User> profileUsers = friendShips.stream()
            .map(friendShip -> (Objects.equals(friendShip.getUserInitiatorId(), user.getUserId()) ? friendShip.getUserReceiveId() : friendShip.getUserInitiatorId()))
            .map(profilePort::takeUserById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

    return friendShips.stream()
            .map(friendShip -> friendShipMapper.toFriendShipUserDto(profileUsers.stream()
                    .filter(profileUser -> profileUser.getUserId().equals(friendShip.getUserInitiatorId()) || profileUser.getUserId().equals(friendShip.getUserReceiveId()))
                    .findFirst()
                    .orElse(null), friendShip.getFriendshipStatus(), (long) friendShipPort.getMutualFriend(user.getUserId(), friendShip.getUserInitiatorId())))
            .toList();
  }

  @Override
  public MessageResponse createRequestFriend(SetRequestFriendRequest setRequestFriendRequest) {
    User user = getUserAuth();
    Long userReceiveId = setRequestFriendRequest.getUserReceiveId();

    if (user.getUserId().equals(userReceiveId)) {
      throw new CustomException("Cannot send request to yourself", HttpStatus.BAD_REQUEST);
    }

    if (!friendShipPort.findUserById(userReceiveId)) {
      throw new CustomException("User not found", HttpStatus.NOT_FOUND);
    }

    EFriendshipStatus requestedStatus = EFriendshipStatus.valueOf(setRequestFriendRequest.getStatus().toUpperCase());

    FriendShip friendShip = friendShipPort.getFriendShip(user.getUserId(), userReceiveId);
    FriendShip friendShipReverse = friendShipPort.getFriendShip(userReceiveId, user.getUserId());

    if (friendShip == null && friendShipReverse == null) {
      return handleNoFriendShips(user, setRequestFriendRequest);
    } else if (friendShip == null) {
      return handleReverseFriendShip(friendShipReverse, requestedStatus);
    } else if (friendShipReverse == null) {
      return handleUserFriendShip(friendShip, requestedStatus);
    } else {
      throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public MessageResponse updateStateFriend(SetRequestFriendRequest setRequestFriendRequest) {
    User user = getUserAuth();
    Long userReceiveId = setRequestFriendRequest.getUserReceiveId();

    if (user.getUserId().equals(userReceiveId)) {
      throw new CustomException("Cannot send request to yourself", HttpStatus.BAD_REQUEST);
    }

    if (profilePort.takeUserById(userReceiveId).isEmpty()) {
      throw new CustomException("User not found", HttpStatus.NOT_FOUND);
    }

    EFriendshipStatus requestedStatus = EFriendshipStatus.valueOf(setRequestFriendRequest.getStatus().toUpperCase());

    FriendShip friendShip = friendShipPort.getFriendShip(user.getUserId(), userReceiveId);
    FriendShip friendShipReverse = friendShipPort.getFriendShip(userReceiveId, user.getUserId());

    if ((friendShip == null && friendShipReverse == null)) {
      throw new CustomException("Friendship not found", HttpStatus.NOT_FOUND);
    } else {
      boolean check = requestedStatus.equals(EFriendshipStatus.PENDING) || requestedStatus.equals(EFriendshipStatus.BLOCK);
      if (friendShip == null) {
        if (check) {
          throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
        }
        friendShipPort.setRequestFriendShip(friendShipReverse.getFriendShipId(), requestedStatus);
        return new MessageResponse("Request sent successfully");

      } else if (friendShipReverse == null) {
        if (check) {
          throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
        }
        friendShipPort.setRequestFriendShip(friendShip.getFriendShipId(), requestedStatus);
        return new MessageResponse("Request sent successfully");
      } else {
        throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
      }
    }
  }

  private MessageResponse handleNoFriendShips(User user, SetRequestFriendRequest setRequestFriendRequest) {
    EFriendshipStatus status = EFriendshipStatus.valueOf(setRequestFriendRequest.getStatus().toUpperCase());
    if (Objects.equals(status, EFriendshipStatus.CLOSE_FRIEND)) {
      friendShipPort.addFriendShip(user.getUserId(), setRequestFriendRequest.getUserReceiveId(), EFriendshipStatus.PENDING);
      return new MessageResponse("Request successfully");
    }
    throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
  }

  private MessageResponse handleUserFriendShip(FriendShip friendShip, EFriendshipStatus requestedStatus) {
    boolean statusNotNull = requestedStatus != null;
    if (statusNotNull && friendShip.getFriendshipStatus().equals(requestedStatus) ||
            (friendShip.getFriendshipStatus().equals(EFriendshipStatus.PENDING) && Objects.equals(requestedStatus, EFriendshipStatus.CLOSE_FRIEND))) {
      throw new CustomException("Request is duplicated", HttpStatus.BAD_REQUEST);
    }
    if (friendShip.getFriendshipStatus().equals(EFriendshipStatus.PENDING) && requestedStatus != EFriendshipStatus.CLOSE_FRIEND) {
      throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }
    if (friendShip.getFriendshipStatus().equals(EFriendshipStatus.BLOCK)) {
      throw new CustomException("This user was blocked", HttpStatus.BAD_REQUEST);
    }
    friendShipPort.setRequestFriendShip(friendShip.getFriendShipId(), requestedStatus);
    return new MessageResponse("Request sent successfully");
  }

  private MessageResponse handleReverseFriendShip(FriendShip friendShip, EFriendshipStatus requestedStatus) {
    boolean statusNotNull = requestedStatus != null;
    if (statusNotNull && friendShip.getFriendshipStatus().equals(requestedStatus)) {
      throw new CustomException("Request is duplicated", HttpStatus.BAD_REQUEST);
    }

    if (friendShip.getFriendshipStatus().equals(EFriendshipStatus.PENDING) && requestedStatus != EFriendshipStatus.CLOSE_FRIEND) {
      throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }
    if (friendShip.getFriendshipStatus().equals(EFriendshipStatus.BLOCK)) {
      throw new CustomException("User was blocked", HttpStatus.FORBIDDEN);
    }

    friendShipPort.setRequestFriendShip(friendShip.getFriendShipId(), requestedStatus);
    return new MessageResponse("Request sent successfully");
  }

  @Override
  public MessageResponse acceptFriendRequest(AcceptFriendRequest acceptFriendRequest) {
    User user = getUserAuth();

    FriendShip friendShip = friendShipPort.getFriendShip(acceptFriendRequest.getFriendId(), user.getUserId());
    if (friendShip == null || !friendShip.getUserReceiveId().equals(user.getUserId())) {
      throw new CustomException("Friendship not found", HttpStatus.NOT_FOUND);
    }

    if (!friendShip.getFriendshipStatus().equals(EFriendshipStatus.PENDING)) {
      throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }

    if (acceptFriendRequest.getIsAccept() == 1) {
      friendShipPort.setRequestFriendShip(friendShip.getFriendShipId(), EFriendshipStatus.CLOSE_FRIEND);
      return new MessageResponse("Request sent successfully");
    } else if (acceptFriendRequest.getIsAccept() == 0) {
      friendShipPort.deleteFriendShip(friendShip.getFriendShipId());
      return new MessageResponse("Request sent successfully");
    }
    throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
  }

  @Override
  public MessageResponse unFriendRequest(UnFriendShipRequest unFriendShipRequest) {
    User user = getUserAuth();

    if (user.getUserId().equals(unFriendShipRequest.getUserId())) {
      throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }

    FriendShip friendShip = friendShipPort.getFriendShip(user.getUserId(), unFriendShipRequest.getUserId());
    FriendShip friendShipReverse = friendShipPort.getFriendShip(unFriendShipRequest.getUserId(), user.getUserId());

    if (friendShip == null && friendShipReverse == null) {
      throw new CustomException("Friendship not found", HttpStatus.NOT_FOUND);
    }

    if (friendShip != null) {
      if ( friendShip.getFriendshipStatus().equals(EFriendshipStatus.BLOCK)) {
        throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
      }
      friendShipPort.deleteFriendShip(friendShip.getFriendShipId());
    }

    if (friendShipReverse != null) {
      if (friendShipReverse.getFriendshipStatus().equals(EFriendshipStatus.PENDING) || friendShipReverse.getFriendshipStatus().equals(EFriendshipStatus.BLOCK)) {
        throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
      }
      friendShipPort.deleteFriendShip(friendShipReverse.getFriendShipId());
    }

    return new MessageResponse("Request sent successfully");
  }

}