package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.SearchPort;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchPortImpl implements SearchPort {
  private final UserRepository userRepository;

  @Override
  public List<User> searchUserInPage(String keyword) {
    return userRepository.searchUsersByNameOrEmail(keyword);
  }
}
