package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.AuthPort;
import com.GHTK.Social_Network.domain.entity.user.Token;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.TokenRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthRepositoryPortImpl implements AuthPort {
  private final TokenRepository tokenRepository;

  private final UserRepository userRepository;

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

  @Override
  public Boolean existsUserByUserEmail(String userEmail) {
    return userRepository.existsByUserEmail(userEmail);
  }

  @Override
  public void changePassword(String newPassword, Long id) {
    userRepository.updatePassword(newPassword, id);
  }

  @Override
  public Optional<Token> findByToken(String jwt) {
    return tokenRepository.findByToken(jwt);
  }
}
