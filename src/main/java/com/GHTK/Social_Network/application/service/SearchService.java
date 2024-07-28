package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.SearchPortInput;
import com.GHTK.Social_Network.application.port.output.SearchPort;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.UserMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService implements SearchPortInput {
  private final SearchPort searchPort;

  private final UserMapper userMapper;

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
}
