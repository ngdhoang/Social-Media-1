package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.user;

import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.auth.JwtPort;
import com.GHTK.Social_Network.application.port.output.auth.RedisAccessTokenPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.UserNode;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.collection.UserCollectionRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.node.UserNodeRepository;
import com.GHTK.Social_Network.infrastructure.mapper.MessageMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.UserCollectionMapperETD;
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
  private final UserNodeRepository userNodeRepository;
  private final UserCollectionRepository userCollectionRepository;

  private final UserMapperETD userMapperETD;
  private final UserCollectionMapperETD userCollectionMapperETD;

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
    UserEntity user = userRepository.findById(accessTokenDto.getUserId()).orElse(null);
    redisAccessTokenPort.createOrUpdate(token + RedisAccessTokenPort.ACCESS_TOKEN_TAIL + user.getUserEmail(), accessTokenDto);
  }

  @Override
  public void saveAllAccessTokenInRedis(String userEmail, Set<Map<String, AccessTokenDto>> tokenEntities) {
    tokenEntities.forEach(tokenMap -> tokenMap.forEach((key, accessTokenDto) -> {
      saveAccessTokenInRedis(key + RedisAccessTokenPort.ACCESS_TOKEN_TAIL + userEmail, accessTokenDto);
    }));
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
  public Optional<User> findByEmail(String input) {
    return Optional.ofNullable(userMapperETD.toDomain(userRepository.findByUserEmail(input).orElse(null)));
  }

  @Override
  public User saveUser(User user) {
    User newUser = userMapperETD.toDomain(userRepository.save(userMapperETD.toEntity(user)));

    UserNode newUserNode = userMapperETD.userDomainToNode(newUser);
    UserNode newUserNodeSave = userNodeRepository.save(newUserNode);

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
