package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.SearchPortInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.ProfilePort;
import com.GHTK.Social_Network.application.port.output.SearchPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.friendShip.EFriendshipStatus;
import com.GHTK.Social_Network.domain.model.friendShip.FriendShip;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.FriendShipMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.UserMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.FriendShipUserDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.SearchUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService implements SearchPortInput {
  private final SearchPort searchPort;

  private final UserMapper userMapper;

  private final AuthPort authPort;

  private final ProfilePort profilePort;

  private final FriendShipPort friendShipPort;

  private final FriendShipMapper friendShipMapper;

  @Override
  public List<FriendShipUserDto> searchUser(SearchUserRequest searchUserRequest) {
    User user = authPort.getUserAuthOrDefaultVirtual();
    if (user.getUserId().equals(0L)) {
      throw new CustomException("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
    String keyword = searchUserRequest.getKeyword();
    if (keyword != null) {
      keyword = keyword.replaceAll("^\\s+", "");
    }

    if (keyword == null || keyword.isEmpty()) {
      return new ArrayList<>();
    }

    List<Long> userIds = searchPort.searchUser(searchUserRequest, user.getUserId());

    return getProfileDtos(userIds, user);

  }

  @Override
  public List<UserBasicDto> searchPublic(String keyword, Integer scope) {
    if (keyword != null) {
      keyword = keyword.replaceAll("^\\s+", "");
    }

    List<UserBasicDto> results = new ArrayList<>();

    List<User> userEntities = searchPort.searchUserInPage(keyword);
    userEntities.forEach(user -> results.add(userMapper.userToUserBasicDto(user)));

    return results;
  }

  private List<FriendShipUserDto> getProfileDtos(List<Long> friendShips, User user) {
    Map<Long, User> profileUsersMap = friendShips.stream()
            .map(profilePort::takeUserById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toMap(User::getUserId, Function.identity()));

    return friendShips.stream()
            .map(friendShipId -> {
              User profileUser = profileUsersMap.get(friendShipId);
              FriendShip friendship = friendShipPort.getFriendShip(user.getUserId(), friendShipId);
              FriendShip friendShipReceived = friendShipPort.getFriendShip(friendShipId, user.getUserId());
              EFriendshipStatus status = friendship != null ? friendship.getFriendshipStatus() : (friendShipReceived != null ? friendShipReceived.getFriendshipStatus() : null);
              long mutualFriends = friendShipPort.getMutualFriendNeo(user.getUserId(), friendShipId);
              return friendShipMapper.toFriendShipUserDto(profileUser, status, mutualFriends);
            })
            .toList();
  }


  @Override
  public List<UserBasicDto> searchFriend(String keyword, PaginationRequest paginationRequest) {
    User user = authPort.getUserAuthOrDefaultVirtual();
    if (user.getUserId().equals(0L)) {
      throw new CustomException("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
    List<Long> users = searchPort.searchFriend(keyword, user.getUserId(), paginationRequest);
    return users.stream().map(profilePort::takeUserById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(userMapper::userToUserBasicDto)
            .toList();
  }
}
