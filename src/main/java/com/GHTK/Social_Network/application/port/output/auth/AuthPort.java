package com.GHTK.Social_Network.application.port.output.auth;

import com.GHTK.Social_Network.domain.model.user.Token;
import com.GHTK.Social_Network.domain.model.user.User;

import java.util.List;
import java.util.Optional;

public interface AuthPort {
  List<Token> findAllValidTokenByUser(Long id);

  void saveToken(Token token);

  void saveAll(List<Token> tokenEntities);

  Optional<User> findByEmail(String input);

  void saveUser(User user);

  Boolean existsUserByUserEmail(String userEmail);

  void changePassword(String newPassword, Long id);

  Optional<Token> findByToken(String jwt);

  void deleteUserByEmail(String email);

  User getUserById(Long id);
}
