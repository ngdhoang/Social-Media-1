package com.GHTK.Social_Network.application.port.output.auth;

import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import com.GHTK.Social_Network.infrastructure.payload.dto.AccessTokenDto;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface AuthPort {
  Optional<User> findByEmail(String input);

  User saveUser(User userEntity);

  Boolean existsUserByUserEmail(String userEmail);

  void changePassword(String newPassword, Long id);

  AccessTokenDto findByToken(String jwt, String email);

  void deleteUserByEmail(String email);

  User getUserById(Long id);

  User getUserAuth();

  User getUserAuthOrDefaultVirtual();

  UserDetailsImpl getUserDetails(User user);

  Pair<UserDetailsImpl, String> refreshToken(String refreshToken, String fingerprinting);

  Set<Map<String, AccessTokenDto>> findAllValidTokenByUser(String userEmail);

  Set<Map<String, AccessTokenDto>>  findAllValidTokenByUser(UserDetailsImpl id);

  void saveAccessTokenInRedis(String token, AccessTokenDto accessTokenDto);

  void saveRefreshTokenInRedis(String token, String fingerprinting, UserDetailsImpl userDetails);

  void saveAllAccessTokenInRedis(String userEmail, Set<Map<String, AccessTokenDto>> tokenEntities);

  void saveAllAccessTokenInRedis(UserDetailsImpl userDetails, Set<Map<String, AccessTokenDto>> tokenEntities);

  UserCollectionDomain getUserCollectionById(Long userId);
}
