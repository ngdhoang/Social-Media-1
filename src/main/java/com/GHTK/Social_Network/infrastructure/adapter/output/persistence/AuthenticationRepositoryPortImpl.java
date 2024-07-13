package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.AuthenticationPort;
import com.GHTK.Social_Network.domain.entity.user.Token;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.domain.repository.TokenRepository;
import com.GHTK.Social_Network.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationRepositoryPortImpl implements AuthenticationPort {
  private final TokenRepository tokenRepository;

  private final UserRepository userRepository;

  public AuthenticationRepositoryPortImpl(TokenRepository tokenRepository, UserRepository userRepository) {
    this.tokenRepository = tokenRepository;
    this.userRepository = userRepository;
  }

  @Override
  public List<Token> findAllValidTokenByUser(Long id) {
    return tokenRepository.findAllValidTokenByUser(id);
  }

  @Override
  public void saveToken(Token token) {
    tokenRepository.save(token);
  }

  @Override
  public void saveAll(List<Token> tokens) {
    tokenRepository.saveAll(tokens);
  }

  @Override
  public Optional<User> findByEmail(String input) {
    return userRepository.findByUserEmail(input);
  }

  @Override
  public void saveUser(User user) {
    userRepository.save(user);
  }
}
