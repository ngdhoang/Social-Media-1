package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.entity.user.Token;
import com.GHTK.Social_Network.domain.entity.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface AuthenticationPort {
  List<Token> findAllValidTokenByUser(Long id);

  void save(Token token);

  void saveAll(List<Token> tokens);

  Optional<User> findByEmail(String input);

  void save(User user);
}
