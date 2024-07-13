package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.entity.user.Token;
import com.GHTK.Social_Network.domain.entity.user.User;

import java.util.List;
import java.util.Optional;

public interface AuthenticationPort {
  List<Token> findAllValidTokenByUser(Long id);

  void saveToken(Token token);

  void saveAll(List<Token> tokens);

  Optional<User> findByEmail(String input);

  void saveUser(User user);
}
