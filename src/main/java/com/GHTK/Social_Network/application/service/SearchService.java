package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.SearchPortInput;
import com.GHTK.Social_Network.application.port.output.SearchPort;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.SearchMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.SearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService implements SearchPortInput {
  private final SearchPort searchPort;

  @Override
  public List<SearchDto> searchPublic(String keyword, Integer scope) {
    if (keyword != null) {
      keyword = keyword.replaceAll("^\\s+", "");
    }

    List<SearchDto> results = new ArrayList<>();

    List<User> users = searchPort.searchUserInPage(keyword);
    users.forEach(user -> {
      results.add(SearchMapper.INSTANCE.UserToSearchDto(user));
    });

    return results;
  }
}
