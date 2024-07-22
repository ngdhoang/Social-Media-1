package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.domain.model.Token;
import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.TokenEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.TokenRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.mapper.TokenMapper;
import com.GHTK.Social_Network.infrastructure.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthAdapter implements AuthPort {
  private final TokenRepository tokenRepository;
  private final UserRepository userRepository;

  private final TokenMapper tokenMapper;
  private final UserMapper userMapper;

  @Override
  public List<Token> findAllValidTokenByUser(Long id) {
    return tokenRepository.findAllValidTokenByUser(id).stream().map(
            tokenMapper::toDomain
    ).toList();
  }

  @Override
  public List<Token> findAllValidTokenByUser(UserDetailsImpl userDetails) {
    return this.findAllValidTokenByUser(userDetails.getUserEntity().getUserId());
  }

  @Override
  public Token saveToken(Token token) {
    TokenEntity newToken = tokenMapper.toEntity(token);
    newToken.setUserEntity(
            userRepository.findById(token.getUserId()).orElse(null)
    );
    return tokenMapper.toDomain(tokenRepository.save(newToken));
  }

  @Override
  public List<Token> saveAllToken(List<Token> tokens) {
    return tokens.stream().map(this::saveToken).toList();
  }

  @Override
  public Optional<User> findByEmail(String input) {
    return Optional.ofNullable(userMapper.toDomain(userRepository.findByUserEmail(input).orElse(null)));
  }

  @Override
  public User saveUser(User user) {
    return userMapper.toDomain(userRepository.save(userMapper.toEntity(user)));
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
    return tokenMapper.toDomain(tokenRepository.findByToken(jwt).orElse(null));
  }

  @Override
  public void deleteUserByEmail(String email) {
    userRepository.delete(userRepository.findByUserEmail(email).orElseThrow());
  }

  @Override
  public User getUserById(Long id) {
    return userMapper.toDomain(userRepository.findById(id).orElse(null));
  }

  @Override
  public User getUserAuth() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username;

    if (principal instanceof UserDetails) {
      username = ((UserDetails) principal).getUsername();
    } else if (principal instanceof String) {
      username = (String) principal;
    } else {
      throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }

    return this.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid token"));
  }

  @Override
  public UserDetailsImpl getUserDetails(User user) {
    return new UserDetailsImpl(userMapper.toEntity(user));
  }
}
