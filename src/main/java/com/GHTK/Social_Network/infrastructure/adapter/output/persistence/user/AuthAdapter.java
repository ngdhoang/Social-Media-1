package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.user;

import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.auth.JwtPort;
import com.GHTK.Social_Network.application.port.output.auth.RedisAccessTokenPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.mapper.UserMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.dto.AccessTokenDto;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class AuthAdapter implements AuthPort {
  private final JwtPort jwtPort;
  private final UserRepository userRepository;
  private final UserMapperETD userMapperETD;

  private final RedisAccessTokenPort redisAccessTokenPort;

  @Override
  public Set<Map<String, AccessTokenDto>> findAllValidTokenByUser(String userEmail) {
    return redisAccessTokenPort.findAllByTail(RedisAccessTokenPort.ACCESS_TOKEN_TAIL + userEmail);
  }

  @Override
  public Set<Map<String, AccessTokenDto>> findAllValidTokenByUser(UserDetailsImpl userDetails) {
    return this.findAllValidTokenByUser(userDetails.getUserEntity().getUserEmail());
  }

  @Override
  public void saveAccessTokenInRedis(String token, AccessTokenDto accessTokenDto) {
    redisAccessTokenPort.createOrUpdate(token + RedisAccessTokenPort.ACCESS_TOKEN_TAIL + accessTokenDto.getUserId(), accessTokenDto);
  }

  @Override
  public void saveAllAccessTokenInRedis(String userEmail, Set<Map<String, AccessTokenDto>> tokenEntities) {
    tokenEntities.forEach(tokenMap -> tokenMap.forEach((key, token) -> {
      saveAccessTokenInRedis(key + RedisAccessTokenPort.ACCESS_TOKEN_TAIL + userEmail, token);
    }));
  }

  @Override
  public void saveAllAccessTokenInRedis(UserDetailsImpl userDetails, Set<Map<String, AccessTokenDto>> tokenEntities) {
    saveAllAccessTokenInRedis(userDetails.getUserEntity().getUserEmail(), tokenEntities);
  }

  @Override
  public Optional<User> findByEmail(String input) {
    return Optional.ofNullable(userMapperETD.toDomain(userRepository.findByUserEmail(input).orElse(null)));
  }

  @Override
  public User saveUser(User user) {
    return userMapperETD.toDomain(userRepository.save(userMapperETD.toEntity(user)));
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
  public AccessTokenDto findByToken(String jwt) {
    return redisAccessTokenPort.findByKey(jwt);
  }

  @Override
  public void deleteUserByEmail(String email) {
    userRepository.delete(userRepository.findByUserEmail(email).orElseThrow());
  }

  @Override
  public User getUserById(Long id) {
    return userMapperETD.toDomain(userRepository.findById(id).orElse(null));
  }

  @Override
  public User getUserAuth() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (isInvalidAuthentication(authentication)) {
      return null;
    }

    String username = extractUsername(authentication.getPrincipal());
    return findByEmail(username).orElse(null);
  }

  @Override
  public User getUserAuthOrDefaultVirtual() {
    User user = getUserAuth();
    return user == null ? User.builder().userId(0L).build() : user;
  }

  @Override
  public UserDetailsImpl getUserDetails(User user) {
    return new UserDetailsImpl(userMapperETD.toEntity(user));
  }

  @Override
  public Pair<UserDetailsImpl, String> refreshToken(String refreshToken, String fingerprinting) {
    String userEmail = jwtPort.extractUserEmail(refreshToken);
    if (userEmail != null) {
      UserEntity user = userRepository.findByUserEmail(userEmail)
              .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
      UserDetailsImpl userDetails = new UserDetailsImpl(user);
      if (jwtPort.isTokenValid(refreshToken, userDetails)) {
        var accessToken = jwtPort.generateToken(userDetails, fingerprinting);
        return Pair.of(userDetails, accessToken);
      }
    }
    return null;
  }

  private boolean isInvalidAuthentication(Authentication authentication) {
    return authentication == null
            || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken;
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
