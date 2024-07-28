package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.user;

import com.GHTK.Social_Network.application.port.output.auth.AuthPort;

import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.user.Token;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.jwt.JwtUtils;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.TokenEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;

import com.GHTK.Social_Network.infrastructure.adapter.output.repository.TokenRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.mapper.TokenMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.UserMapperETD;
import lombok.AllArgsConstructor;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class AuthAdapter implements AuthPort {
  private final JwtUtils jwtUtils;


  private final TokenRepository tokenRepository;
  private final UserRepository userRepository;

  private final TokenMapperETD tokenMapperETD;
  private final UserMapperETD userMapperETD;

  @Override
  public List<Token> findAllValidTokenByUser(Long id) {
    return tokenRepository.findAllValidTokenByUser(id).stream().map(
            tokenMapperETD::toDomain
    ).toList();
  }

  @Override
  public List<Token> findAllValidTokenByUser(UserDetailsImpl userDetails) {
    return this.findAllValidTokenByUser(userDetails.getUserEntity().getUserId());
  }

  @Override
  public Token saveToken(Token token) {
    TokenEntity newToken = tokenMapperETD.toEntity(token);
    newToken.setUserEntity(
            userRepository.findById(token.getUserId()).orElse(null)
    );
    return tokenMapperETD.toDomain(tokenRepository.save(newToken));
  }

  @Override
  public List<Token> saveAllToken(List<Token> tokens) {
    return tokens.stream().map(this::saveToken).toList();
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
  public Token findByToken(String jwt) {
    return tokenMapperETD.toDomain(tokenRepository.findByToken(jwt).orElse(null));
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
  public Pair<UserDetailsImpl, String> refreshToken(String refreshToken) {
    String userEmail = jwtUtils.extractUserEmail(refreshToken);
    if (userEmail != null) {
      UserEntity user = userRepository.findByUserEmail(userEmail)
              .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
      UserDetailsImpl userDetails = new UserDetailsImpl(user);
      if (jwtUtils.isTokenValid(refreshToken, userDetails)) {
        var accessToken = jwtUtils.generateToken(userDetails);
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
