package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.FriendShipPortInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.MapperEntity.UserMapper;
import com.GHTK.Social_Network.infrastructure.entity.EFriendshipStatusEntity;
import com.GHTK.Social_Network.infrastructure.entity.FriendShipEntity;
import com.GHTK.Social_Network.infrastructure.entity.user.UserEntity;
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

  private final Map<EFriendshipStatusEntity, Integer> status = Map.of(
          EFriendshipStatusEntity.PENDING, 0,
          EFriendshipStatusEntity.CLOSE_FRIEND, 1,
          EFriendshipStatusEntity.SIBLING, 2,
          EFriendshipStatusEntity.PARENT, 3,
          EFriendshipStatusEntity.BLOCK, 4,
          EFriendshipStatusEntity.OTHER, 5
  );
  private final AuthPort authenticationRepositoryPort;

  private final FriendShipPort friendShipPort;

  private final ProfilePort profilePort;

  private UserEntity getUserAuth() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username;

    if (principal instanceof UserDetails) {
      username = ((UserDetails) principal).getUsername();
    } else if (principal instanceof String) {
      username = (String) principal;
    } else {
      throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }

    return UserMapper.INSTANCE.toEntity(authenticationRepositoryPort.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid token")));
  }

  @Override
  public List<ProfileDto> getFriendShip(GetFriendShipRequest getFriendShipRequest) {
    UserEntity userEntity = getUserAuth();

    if (getFriendShipRequest.getUserId() == null || getFriendShipRequest.getUserId().equals(userEntity.getUserId())) {
      getFriendShipRequest.setUserId(userEntity.getUserId());
      List<FriendShipEntity> friendShips = friendShipPort.getListFriendShip(getFriendShipRequest);
      if (getFriendShipRequest.getStatus() != null && getFriendShipRequest.getStatus().equals(EFriendshipStatusEntity.BLOCK)) {
        List<UserEntity> profileUserEntity = friendShips.stream()
                .map(friendShip -> profilePort.takeProfileById(friendShip.getUserReceiveId())
                        .orElseThrow(() -> new CustomException("Not found", HttpStatus.NOT_FOUND)))
                .toList();
        return profileUserEntity.stream()
                .map(ProfileMapper.INSTANCE::userToProfileDto)
                .toList();
      }
      return getProfileDtos(userEntity, friendShips);
    }

    if (getFriendShipRequest.getStatus() == EFriendshipStatusEntity.PENDING) {
      throw new CustomException("Not permission", HttpStatus.FORBIDDEN);
    }
    UserEntity userEntityReceive = profilePort.takeProfileById(getFriendShipRequest.getUserId())
            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

    if (!userEntity.getUserId().equals(userEntityReceive.getUserId()) && !userEntityReceive.getIsProfilePublic()) {
      throw new CustomException("Not permission", HttpStatus.FORBIDDEN);
    }

    FriendShipEntity friendShip = friendShipPort.getFriendShip(userEntity.getUserId(), userEntityReceive.getUserId());
    FriendShipEntity friendShipReverse = friendShipPort.getFriendShip(userEntityReceive.getUserId(), userEntity.getUserId());

    if ((friendShip == null && friendShipReverse == null)
            || (friendShip != null && friendShip.getFriendshipStatus().equals(EFriendshipStatusEntity.BLOCK))
            || (friendShipReverse != null && friendShipReverse.getFriendshipStatus().equals(EFriendshipStatusEntity.BLOCK))
            || (friendShip != null && friendShip.getFriendshipStatus().equals(EFriendshipStatusEntity.PENDING)
            || (friendShipReverse != null && friendShipReverse.getFriendshipStatus().equals(EFriendshipStatusEntity.PENDING)))) {
      throw new CustomException("Not permission", HttpStatus.FORBIDDEN);
    }

    if (getFriendShipRequest.equals(EFriendshipStatusEntity.BLOCK)) {
      throw new CustomException("Not permission", HttpStatus.FORBIDDEN);
    }

    List<FriendShipEntity> friendShips = friendShipPort.getListFriendShip(getFriendShipRequest);
    return getProfileDtos(userEntity, friendShips);

  }

  private List<ProfileDto> getProfileDtos(UserEntity userEntity, List<FriendShipEntity> friendShips) {
    List<UserEntity> profileUserEntities = friendShips.stream()
            .map(friendShip -> (friendShip.getUserInitiatorId().equals(userEntity.getUserId()) ? friendShip.getUserReceiveId() : friendShip.getUserInitiatorId()))
            .map(profilePort::takeProfileById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    return profileUserEntities.stream()
            .map(ProfileMapper.INSTANCE::userToProfileDto)
            .toList();
  }

  @Override
  public MessageResponse setRequestFriendShip(SetRequestFriendRequest setRequestFriendRequest) {
    UserEntity userEntity = getUserAuth();
    Long userReceiveId = setRequestFriendRequest.getUserReceiveId();

    if (!friendShipPort.findUserById(userReceiveId)) {
      throw new CustomException("User not found", HttpStatus.NOT_FOUND);
    }

    EFriendshipStatusEntity requestedStatus = getKey(status, setRequestFriendRequest.getStatus());

    if (userEntity.getUserId().equals(userReceiveId)) {
      throw new CustomException("Invalid request: Cannot send request to yourself", HttpStatus.BAD_REQUEST);
    }

    FriendShipEntity friendShip = friendShipPort.getFriendShip(userEntity.getUserId(), userReceiveId);
    FriendShipEntity friendShipReverse = friendShipPort.getFriendShip(userReceiveId, userEntity.getUserId());

    if (friendShip == null && friendShipReverse == null) {
      return handleNoFriendShips(userEntity, setRequestFriendRequest);
    } else if (friendShip == null) {
      return handleReverseFriendShip(friendShipReverse, userEntity, setRequestFriendRequest, requestedStatus);
    } else if (friendShipReverse == null) {
      return handleUserFriendShip(friendShip, userEntity, setRequestFriendRequest, requestedStatus);
    } else {
      handleBothFriendShips(friendShip, friendShipReverse, userEntity, setRequestFriendRequest, requestedStatus);
    }
    return null;
  }

  private MessageResponse handleNoFriendShips(UserEntity userEntity, SetRequestFriendRequest setRequestFriendRequest) {
    Integer status = setRequestFriendRequest.getStatus();
    if (Objects.equals(status, this.status.get(EFriendshipStatusEntity.BLOCK))) {
      friendShipPort.addFriendShip(userEntity.getUserId(), setRequestFriendRequest.getUserReceiveId(), EFriendshipStatusEntity.BLOCK);
      return new MessageResponse("Request successfully");
    }
    if (Objects.equals(status, this.status.get(EFriendshipStatusEntity.CLOSE_FRIEND))) {
      friendShipPort.addFriendShip(userEntity.getUserId(), setRequestFriendRequest.getUserReceiveId(), EFriendshipStatusEntity.PENDING);
      return new MessageResponse("Request successfully");
    }
    throw new CustomException("Request is invalid", HttpStatus.BAD_REQUEST);
  }


  private MessageResponse handleUserFriendShip(FriendShipEntity friendShip, UserEntity userEntity, SetRequestFriendRequest setRequestFriendRequest, EFriendshipStatusEntity requestedStatus) {
    Boolean statusNotNull = requestedStatus != null;
    if (statusNotNull && friendShip.getFriendshipStatus().equals(requestedStatus) ||
            (friendShip.getFriendshipStatus().equals(EFriendshipStatusEntity.PENDING) && requestedStatus.equals(EFriendshipStatusEntity.CLOSE_FRIEND))) {
      throw new CustomException("Request is duplicated", HttpStatus.BAD_REQUEST);
    }
    if (friendShip.getFriendshipStatus().equals(EFriendshipStatusEntity.PENDING) &&
            requestedStatus != EFriendshipStatusEntity.CLOSE_FRIEND && requestedStatus != EFriendshipStatusEntity.BLOCK) {
      throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }
    if (friendShip.getFriendshipStatus().equals(EFriendshipStatusEntity.BLOCK) &&
            requestedStatus != EFriendshipStatusEntity.BLOCK) {
      throw new CustomException("This user was blocked", HttpStatus.BAD_REQUEST);
    }
    friendShipPort.setRequestFriendShip(friendShip.getFriendShipId(), requestedStatus);
    return new MessageResponse("Request sent successfully");
  }

  private MessageResponse handleReverseFriendShip(FriendShipEntity friendShip, UserEntity userEntity, SetRequestFriendRequest setRequestFriendRequest, EFriendshipStatusEntity requestedStatus) {
    Boolean statusNotNull = requestedStatus != null;
    if (statusNotNull && friendShip.getFriendshipStatus().equals(requestedStatus) &&
            requestedStatus != EFriendshipStatusEntity.BLOCK) {
      throw new CustomException("Request is duplicated", HttpStatus.BAD_REQUEST);
    }

    if (friendShip.getFriendshipStatus().equals(EFriendshipStatusEntity.PENDING) &&
            requestedStatus != EFriendshipStatusEntity.CLOSE_FRIEND && requestedStatus != EFriendshipStatusEntity.BLOCK) {
      throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }
    if (friendShip.getFriendshipStatus().equals(EFriendshipStatusEntity.BLOCK) &&
            requestedStatus != EFriendshipStatusEntity.BLOCK) {
      throw new CustomException("User was blocked", HttpStatus.FORBIDDEN);
    }
    if (requestedStatus == EFriendshipStatusEntity.BLOCK) {
      friendShipPort.addFriendShip(userEntity.getUserId(), setRequestFriendRequest.getUserReceiveId(), EFriendshipStatusEntity.BLOCK);
      friendShipPort.deleteFriendShip(friendShip.getFriendShipId());
      return new MessageResponse("Request successfully");
    }
    friendShipPort.setRequestFriendShip(friendShip.getFriendShipId(), requestedStatus);
    return new MessageResponse("Request sent successfully");
  }

  private void handleBothFriendShips(FriendShipEntity friendShip, FriendShipEntity friendShipReverse, UserEntity userEntity, SetRequestFriendRequest setRequestFriendRequest, EFriendshipStatusEntity requestedStatus) {
    if (friendShip.getFriendshipStatus().equals(requestedStatus)) {
      throw new CustomException("Request is duplicated", HttpStatus.BAD_REQUEST);
    }
    if (setRequestFriendRequest.getStatus() != status.get(EFriendshipStatusEntity.BLOCK)
    ) {
      throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public MessageResponse acceptRequestFriendShip(AcceptFriendRequest acceptFriendRequest) {
    FriendShipEntity friendShip = friendShipPort.getFriendShipById(acceptFriendRequest.getFriendId());

    if (friendShip == null || !friendShip.getFriendshipStatus().equals(EFriendshipStatusEntity.PENDING)) {
      throw new CustomException("Friendship not found", HttpStatus.NOT_FOUND);
    }
    if (acceptFriendRequest.getIsAccept() == 1) {
      friendShipPort.setRequestFriendShip(acceptFriendRequest.getFriendId(), EFriendshipStatusEntity.CLOSE_FRIEND);
      return new MessageResponse("Request sent successfully");
    } else if (acceptFriendRequest.getIsAccept() == 0) {
      friendShipPort.deleteFriendShip(acceptFriendRequest.getFriendId());
      return new MessageResponse("Request sent successfully");
    }
    throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
  }

  @Override
  public MessageResponse unFriendShip(UnFriendShipRequest unFriendShipRequest) {
    UserEntity userEntity = getUserAuth();
    if (userEntity.getUserId().equals(unFriendShipRequest.getFriendId())) {
      throw new CustomException("Invalid request", HttpStatus.BAD_REQUEST);
    }

    FriendShipEntity friendShip = friendShipPort.getFriendShip(userEntity.getUserId(), unFriendShipRequest.getFriendId());

    if (friendShip == null || (!friendShip.getUserReceiveId().equals(userEntity.getUserId()) && !friendShip.getUserInitiatorId().equals(userEntity.getUserId()))) {
      throw new CustomException("Friendship not found", HttpStatus.NOT_FOUND);
    }
    friendShipPort.deleteFriendShip(friendShip.getFriendShipId());
    return new MessageResponse("Request sent successfully");
  }

  private EFriendshipStatusEntity getKey(Map<EFriendshipStatusEntity, Integer> map, Integer value) {
    for (Map.Entry<EFriendshipStatusEntity, Integer> entry : map.entrySet()) {
      if (entry.getValue().equals(value)) {
        return entry.getKey();
      }
    }
    return null;
  }

}