package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.user;

import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.auth.JwtPort;
import com.GHTK.Social_Network.application.port.output.auth.redis.RedisAccessTokenPort;
import com.GHTK.Social_Network.application.port.output.auth.redis.RedisRefreshTokenPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.collection.UserCollectionRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.node.UserNodeRepository;
import com.GHTK.Social_Network.infrastructure.mapper.UserCollectionMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.UserMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.dto.AccessTokenDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthAdapter implements AuthPort {
  private final JwtPort jwtPort;
  private final UserRepository userRepository;
  private final UserNodeRepository userNodeRepository;
  private final UserCollectionRepository userCollectionRepository;

  private final RedisAccessTokenPort redisAccessTokenPort;
  private final RedisRefreshTokenPort redisRefreshTokenPort;

  private final UserMapperETD userMapperETD;
  private final UserCollectionMapperETD userCollectionMapperETD;

  @Override
  public Set<Map<String, AccessTokenDto>> findAllValidTokenByUser(String userEmail) {
    return redisAccessTokenPort.getKeyValueByPattern("*" + RedisAccessTokenPort.ACCESS_TOKEN_TAIL + userEmail);
  }

  @Override
  public Set<Map<String, AccessTokenDto>> findAllValidTokenByUser(UserDetailsImpl userDetails) {
    return findAllValidTokenByUser(userDetails.getUserEntity().getUserEmail());
  }

  @Override
  public void saveAccessTokenInRedis(String token, AccessTokenDto accessTokenDto) {
    userRepository.findById(accessTokenDto.getUserId()).ifPresent(user ->
            redisAccessTokenPort.createOrUpdate(token + RedisAccessTokenPort.ACCESS_TOKEN_TAIL + user.getUserEmail(), accessTokenDto)
    );
  }

  @Override
  public void saveRefreshTokenInRedis(String token, String fingerprinting, UserDetailsImpl userDetails) {
    redisRefreshTokenPort.createOrUpdate(
            token + RedisRefreshTokenPort.REFRESH_TOKEN + fingerprinting + RedisRefreshTokenPort.REFRESH_TOKEN + userDetails.getUserEntity().getUserEmail(),
            null
    );
  }

  @Override
  public void saveAllAccessTokenInRedis(String userEmail, Set<Map<String, AccessTokenDto>> tokenEntities) {
    tokenEntities.forEach(tokenMap -> tokenMap.forEach((key, accessTokenDto) ->
            saveAccessTokenInRedis(key + RedisAccessTokenPort.ACCESS_TOKEN_TAIL + userEmail, accessTokenDto)
    ));
  }

  @Override
  public void saveAllAccessTokenInRedis(UserDetailsImpl userDetails, Set<Map<String, AccessTokenDto>> tokenEntities) {
    saveAllAccessTokenInRedis(userDetails.getUserEntity().getUserEmail(), tokenEntities);
  }

  @Override
  public UserCollectionDomain getUserCollectionById(Long userId) {
    return userCollectionMapperETD.toDomain(userCollectionRepository.findByUserId(userId));
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userRepository.findByUserEmail(email).map(userMapperETD::toDomain);
  }

  @Override
  public User saveUser(User user) {
    User newUser = userMapperETD.toDomain(userRepository.save(userMapperETD.toEntity(user)));
    return newUser;
  }

  @Override
  public Boolean existsUserByUserEmail(String userEmail) {
    return userRepository.existsByUserEmail(userEmail);
  }

  @Override
  public void changePassword(String newPassword, Long id) {
    userRepository.updatePassword(newPassword, id);
  }

  @Override
  public AccessTokenDto findByToken(String jwt, String email) {
    return redisAccessTokenPort.findByKey(jwt + RedisAccessTokenPort.ACCESS_TOKEN_TAIL + email);
  }

  @Override
  public void deleteUserByEmail(String email) {
    userRepository.findByUserEmail(email).ifPresent(userRepository::delete);
  }

  @Override
  public User getUserById(Long id) {
    return userRepository.findById(id).map(userMapperETD::toDomain).orElse(null);
  }

  @Override
  public User getUserByIdOrDefault(Long id) {
    return userRepository.findById(id).map(userMapperETD::toDomain).orElseGet(
            () -> new User("", "Tài khoản này không tồn tại", "", "")
    );
  }

  @Override
  public User getUserAuth() {
    return getAuthenticatedUser().flatMap(this::findByEmail).orElse(null);
  }

  @Override
  public User getUserAuthOrDefaultVirtual() {
    return Optional.ofNullable(getUserAuth()).orElse(User.builder().userId(0L).build());
  }

  @Override
  public UserDetailsImpl getUserDetails(User user) {
    return new UserDetailsImpl(userMapperETD.toEntity(user));
  }

  @Override
  public Pair<UserDetailsImpl, String> refreshToken(String refreshToken, String fingerprinting) {
    return Optional.ofNullable(jwtPort.extractUserEmail(refreshToken))
            .flatMap(userEmail -> userRepository.findByUserEmail(userEmail)
                    .map(user -> {
                      UserDetailsImpl userDetails = new UserDetailsImpl(user);
                      if (jwtPort.isTokenValid(refreshToken, userDetails)) {
                        String accessToken = jwtPort.generateToken(userDetails, fingerprinting);
                        return Pair.of(userDetails, accessToken);
                      }
                      return null;
                    }))
            .orElseThrow(() -> new CustomException("Invalid token or user not found", HttpStatus.NOT_FOUND));
  }

  private Optional<String> getAuthenticatedUser() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(auth -> auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken))
            .map(Authentication::getPrincipal)
            .map(this::extractUsername);
  }

  private String extractUsername(Object principal) {
    if (principal instanceof UserDetails) {
      return ((UserDetails) principal).getUsername();
    }
    if (principal instanceof String) {
      return (String) principal;
    }
    throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
  }
}