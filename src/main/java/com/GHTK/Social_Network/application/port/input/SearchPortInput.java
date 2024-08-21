package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.dto.FriendShipUserDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.SearchUserRequest;

import java.util.List;

public interface SearchPortInput {
  List<FriendShipUserDto> searchUser(SearchUserRequest searchUserRequest);

  List<UserBasicDto> searchPublic(String keyword, Integer scope);
}
