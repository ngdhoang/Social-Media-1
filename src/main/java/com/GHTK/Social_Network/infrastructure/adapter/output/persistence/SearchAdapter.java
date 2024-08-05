package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.SearchPort;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.mapper.UserMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchAdapter implements SearchPort {
  private final UserRepository userRepository;

  private final UserMapperETD userMapperETD;

  @Override
  public List<User> searchUserInPage(String keyword) {
    return userRepository.searchUsersByNameOrEmail(keyword).stream().map(
            userMapperETD::toDomain
    ).toList();
  }
}
