package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.domain.model.user.Token;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.TokenRepositoryAdapter;
import com.GHTK.Social_Network.infrastructure.adapter.output.UserRepositoryAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthRepositoryPortImpl implements AuthPort {
  private final TokenRepositoryAdapter tokenEntityRepository;

  private final UserRepositoryAdapter userRepository;

  @Override
  public List<Token> findAllValidTokenByUser(Long id) {
    return tokenEntityRepository.findAllValidTokenByUser(id);
  }

  @Override
  public void saveToken(Token tokenEntity) {
    tokenEntityRepository.save(tokenEntity);
  }

  @Override
  public void saveAll(List<Token> tokenEntities) {
    tokenEntityRepository.saveAll(tokenEntities);
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
    return tokenEntityRepository.findByToken(jwt);
  }

  @Override
  public void deleteUserByEmail(String email) {
    userRepository.delete(userRepository.findByUserEmail(email).orElseThrow());
  }

  @Override
  public User getUserById(Long id) {
    return userRepository.findById(id).orElse(null);
  }
}
