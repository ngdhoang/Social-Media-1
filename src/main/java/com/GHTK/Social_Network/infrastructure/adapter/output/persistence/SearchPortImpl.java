package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.SearchPort;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchPortImpl implements SearchPort {
  private final UserRepository userEntityRepository;

  @Override
  public List<User> searchUserInPage(String keyword) {
    return userEntityRepository.searchUsersByNameOrEmail(keyword);
  }
}
