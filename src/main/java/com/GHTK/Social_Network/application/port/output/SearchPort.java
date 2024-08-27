package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.SearchUserRequest;

import java.util.List;

public interface SearchPort {
  List<User> searchUserInPage(String keyword);

  List<Long> searchUser(SearchUserRequest searchUserRequest, Long userId);

  List<Long> searchFriend(String keyword, Long userId, PaginationRequest paginationRequest);
}
