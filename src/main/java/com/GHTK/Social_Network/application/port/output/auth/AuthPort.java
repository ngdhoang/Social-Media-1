package com.GHTK.Social_Network.application.port.output.auth;

import com.GHTK.Social_Network.domain.model.Token;
import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;

import java.util.List;
import java.util.Optional;

public interface AuthPort {
  List<Token> findAllValidTokenByUser(Long id);

  List<Token> findAllValidTokenByUser(UserDetailsImpl id);

  Token saveToken(Token tokenEntity);

  List<Token> saveAllToken(List<Token> tokenEntities);

  Optional<User> findByEmail(String input);

  User saveUser(User userEntity);

  Boolean existsUserByUserEmail(String userEmail);

  void changePassword(String newPassword, Long id);

  Token findByToken(String jwt);

  void deleteUserByEmail(String email);

  User getUserById(Long id);

  User getUserAuth();

  UserDetailsImpl getUserDetails(User user);
}
