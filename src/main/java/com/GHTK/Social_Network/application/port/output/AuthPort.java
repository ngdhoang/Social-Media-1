package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.entity.user.Token;
import com.GHTK.Social_Network.domain.entity.user.User;

import java.util.List;
import java.util.Optional;

public interface AuthPort {
  List<Token> findAllValidTokenByUser(Long id);

  void saveToken(Token token);

  void saveAll(List<Token> tokens);

  Optional<User> findByEmail(String input);

  void saveUser(User user);

  Boolean existsUserByUserEmail(String userEmail);

  void changePassword(String newPassword, Long id);

  Optional<Token> findByToken(String jwt);

}
