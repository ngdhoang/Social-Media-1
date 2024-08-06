package com.GHTK.Social_Network.application.port.output.auth;

import com.GHTK.Social_Network.domain.model.user.Token;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import org.apache.commons.lang3.tuple.Pair;

import java.security.Principal;
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

  User getUserAuthOrDefaultVirtual();

  UserDetailsImpl getUserDetails(User user);

  Pair<UserDetailsImpl, String> refreshToken(String refreshToken, String fingerprinting);

  Pair<Long, UserDetailsImpl> getUserDetails(Principal principal);
}
