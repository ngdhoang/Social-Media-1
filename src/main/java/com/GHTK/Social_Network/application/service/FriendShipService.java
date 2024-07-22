package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.FriendShipPortInput;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.domain.model.EFriendshipStatus;
import com.GHTK.Social_Network.domain.model.FriendShip;
import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.EFriendshipStatus;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.FriendShipEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.User;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ProfileMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.AcceptFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetFriendShipRequest;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
  private final AuthPort authPort;
  private final FriendShipPort friendShipPort;
  private final ProfilePort profilePort;

  private final ProfileMapper profileMapper;


  @Override
  public List<ProfileDto> getFriendShip(GetFriendShipRequest getFriendShipRequest) {
    User user = authPort.getUserAuth();

    if (getFriendShipRequest.getUserId() == null || getFriendShipRequest.getUserId().equals(user.getUserId())) {
      getFriendShipRequest.setUserId(user.getUserId());
      List<FriendShip> friendShips = friendShipPort.getListFriendShip(getFriendShipRequest);
      if (getFriendShipRequest.getStatus() != null && getFriendShipRequest.getStatus().equals(EFriendshipStatus.BLOCK)) {
        List<User> profileUser = friendShips.stream()
                .map(friendShip -> profilePort.takeProfileById(friendShip.getUserReceiveId())
                        .orElseThrow(() -> new CustomException("Not found", HttpStatus.NOT_FOUND)))
                .toList();
        return profileUser.stream()
                .map(profileMapper::userToProfileDto)
                .toList();
      }
      return getProfileDtos(user, friendShips);
    }

    if(getFriendShipRequest.getStatus() == EFriendshipStatus.PENDING){
      throw new CustomException("Not permission", HttpStatus.FORBIDDEN);
    }
    User userReceive = profilePort.takeProfileById(getFriendShipRequest.getUserId())
            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

    if (!user.getUserId().equals(userReceive.getUserId()) && !userReceive.getIsProfilePublic()) {
      throw new CustomException("Not permission", HttpStatus.FORBIDDEN);
    }

    FriendShip friendShip = friendShipPort.getFriendShip(user.getUserId(), userReceive.getUserId());
    FriendShip friendShipReverse = friendShipPort.getFriendShip(userReceive.getUserId(), user.getUserId());


    if ((friendShip == null && friendShipReverse == null)
            || (friendShip != null && friendShip.getFriendshipStatus().equals(EFriendshipStatus.BLOCK))
            || (friendShipReverse != null && friendShipReverse.getFriendshipStatus().equals(EFriendshipStatus.BLOCK))
            || (friendShip != null && friendShip.getFriendshipStatus().equals(EFriendshipStatus.PENDING)
            || (friendShipReverse != null && friendShipReverse.getFriendshipStatus().equals(EFriendshipStatus.PENDING)))) {
      throw new CustomException("Not permission", HttpStatus.FORBIDDEN);
    }

    if (getFriendShipRequest.getStatus().equals(EFriendshipStatus.BLOCK)) {
      throw new CustomException("Not permission", HttpStatus.FORBIDDEN);
    }

    List<FriendShip> friendShips = friendShipPort.getListFriendShip(getFriendShipRequest);
    return getProfileDtos(user, friendShips);

  }

  private List<ProfileDto> getProfileDtos(User user, List<FriendShip> friendShips) {
    List<User> profileUsers = friendShips.stream()
            .map(friendShip -> (friendShip.getUserInitiatorId().equals(user.getUserId()) ? friendShip.getUserReceiveId() : friendShip.getUserInitiatorId()))
            .map(profilePort::takeProfileById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    return profileUsers.stream()
            .map(ProfileMapper.INSTANCE::userToProfileDto)
            .toList();
  }

  @Override
  public MessageResponse setRequestFriendShip(SetRequestFriendRequest setRequestFriendRequest) {
    User user = authPort.getUserAuth();
    Long userReceiveId = setRequestFriendRequest.getUserReceiveId();

    if (!friendShipPort.findUserById(userReceiveId)) {
      throw new CustomException("User not found", HttpStatus.NOT_FOUND);
    }

    EFriendshipStatus requestedStatus = getKey(status, setRequestFriendRequest.getStatus());

    if (user.getUserId().equals(userReceiveId)) {
      throw new CustomException("Invalid request: Cannot send request to yourself", HttpStatus.BAD_REQUEST);
    }

    FriendShip friendShip = friendShipPort.getFriendShip(user.getUserId(), userReceiveId);
    FriendShip friendShipReverse = friendShipPort.getFriendShip(userReceiveId, user.getUserId());

    if (friendShip == null && friendShipReverse == null) {
      return handleNoFriendShips(user, setRequestFriendRequest);
    } else if (friendShip == null) {
      return handleReverseFriendShip(friendShipReverse, user, setRequestFriendRequest, requestedStatus);
    } else if (friendShipReverse == null) {
      return handleUserFriendShip(friendShip, user, setRequestFriendRequest, requestedStatus);
    } else {
      handleBothFriendShips(friendShip, friendShipReverse, user, setRequestFriendRequest, requestedStatus);
    }
    return null;
  }

  private MessageResponse handleNoFriendShips(User user, SetRequestFriendRequest setRequestFriendRequest) {
    Integer status = setRequestFriendRequest.getStatus();
    if (Objects.equals(status, this.status.get(EFriendshipStatus.BLOCK))) {
      friendShipPort.addFriendShip(user.getUserId(), setRequestFriendRequest.getUserReceiveId(), EFriendshipStatus.BLOCK);
      return new MessageResponse("Request successfully");
    }
    if (Objects.equals(status, this.status.get(EFriendshipStatus.CLOSE_FRIEND))) {
      friendShipPort.addFriendShip(user.getUserId(), setRequestFriendRequest.getUserReceiveId(), EFriendshipStatus.PENDING);
      return new MessageResponse("Request successfully");
    }
    throw new CustomException("Request is invalid", HttpStatus.BAD_REQUEST);
  }


  private MessageResponse handleUserFriendShip(FriendShip friendShip, User user, SetRequestFriendRequest setRequestFriendRequest, EFriendshipStatus requestedStatus) {
    boolean statusNotNull = requestedStatus != null;
    if (statusNotNull && friendShip.getFriendshipStatus().equals(requestedStatus) ||
            (friendShip.getFriendshipStatus().equals(EFriendshipStatus.PENDING) && Objects.equals(requestedStatus, EFriendshipStatus.CLOSE_FRIEND))) {
      throw new CustomException("Request is duplicated", HttpStatus.BAD_REQUEST);
    }
    if (friendShipEntity.getFriendshipStatus().equals(EFriendshipStatus.PENDING) &&
            requestedStatus != EFriendshipStatus.CLOSE_FRIEND && requestedStatus != EFriendshipStatus.BLOCK) {
      throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }
    if (friendShipEntity.getFriendshipStatus().equals(EFriendshipStatus.BLOCK) &&
            requestedStatus != EFriendshipStatus.BLOCK) {
      throw new CustomException("This user was blocked", HttpStatus.BAD_REQUEST);
    }
    friendShipPort.setRequestFriendShip(friendShip.getFriendShipId(), requestedStatus);
    return new MessageResponse("Request sent successfully");
  }

  private MessageResponse handleReverseFriendShip(FriendShip friendShip, User user, SetRequestFriendRequest setRequestFriendRequest, EFriendshipStatus requestedStatus) {
    boolean statusNotNull = requestedStatus != null;
    if (statusNotNull && friendShip.getFriendshipStatus().equals(requestedStatus) &&
            requestedStatus != EFriendshipStatus.BLOCK) {
      throw new CustomException("Request is duplicated", HttpStatus.BAD_REQUEST);
    }

    if (friendShip.getFriendshipStatus().equals(EFriendshipStatus.PENDING) &&
            requestedStatus != EFriendshipStatus.CLOSE_FRIEND && requestedStatus != EFriendshipStatus.BLOCK) {
      throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }
    if (friendShip.getFriendshipStatus().equals(EFriendshipStatus.BLOCK) &&
            requestedStatus != EFriendshipStatus.BLOCK) {
      throw new CustomException("User was blocked", HttpStatus.FORBIDDEN);
    }
    if (requestedStatus == EFriendshipStatus.BLOCK) {
      friendShipPort.addFriendShip(userEntity.getUserId(), setRequestFriendRequest.getUserReceiveId(), EFriendshipStatus.BLOCK);
      friendShipPort.deleteFriendShip(friendShip.getFriendShipId());
      return new MessageResponse("Request successfully");
    }
    friendShipPort.setRequestFriendShip(friendShip.getFriendShipId(), requestedStatus);
    return new MessageResponse("Request sent successfully");
  }

  private void handleBothFriendShips(FriendShip friendShip, FriendShip friendShipReverse, User user, SetRequestFriendRequest setRequestFriendRequest, EFriendshipStatus requestedStatus) {
    if (friendShip.getFriendshipStatus().equals(requestedStatus)) {
      throw new CustomException("Request is duplicated", HttpStatus.BAD_REQUEST);
    }
    if (!Objects.equals(setRequestFriendRequest.getStatus(), status.get(EFriendshipStatus.BLOCK))
    ) {
      throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public MessageResponse acceptRequestFriendShip(AcceptFriendRequest acceptFriendRequest) {
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
  public MessageResponse unFriendShip(UnFriendShipRequest unFriendShipRequest) {
    User userEntity = authPort.getUserAuth();
    if (userEntity.getUserId().equals(unFriendShipRequest.getFriendId())) {
      throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }

    FriendShip friendShip = friendShipPort.getFriendShip(userEntity.getUserId(), unFriendShipRequest.getFriendId());

    if (friendShip == null || (!friendShip.getUserReceiveId().equals(userEntity.getUserId()) && !friendShip.getUserInitiatorId().equals(userEntity.getUserId()))) {
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