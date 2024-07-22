package com.GHTK.Social_Network.infrastructure.adapter.output;

import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.domain.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.MapperEntity.UserMapper;
import com.GHTK.Social_Network.infrastructure.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
  private final UserEntityRepository userEntityRepository;

  @Override
  public User findByUserEmail(String userEmail) {
    return UserMapper.INSTANCE.toDomain(userEntityRepository.findByUserEmail(userEmail));
  }

  @Override
  public Boolean existsByUserEmail(String userEmail) {
    return userEntityRepository.existsByUserEmail(userEmail);
  }

  @Override
  public void updatePassword(String hashedPassword, Long id) {
    userEntityRepository.updatePassword(hashedPassword, id);
  }

  @Override
  public int changeStateProfile(Boolean state, Long userId) {
    return userEntityRepository.changeStateProfile(state, userId);
  }

  @Override
  public int changeAvatar(String url, Long userId) {
    return userEntityRepository.changeAvatar(url, userId);
  }

  @Override
  public List<User> searchUsersByNameOrEmail(String name) {
    return userEntityRepository.searchUsersByNameOrEmail(name)
            .stream()
            .map(UserMapper.INSTANCE::toDomain)
            .toList();
  }

  @Override
  public User save(User user) {
    return UserMapper.INSTANCE.toDomain(userEntityRepository.save(UserMapper.INSTANCE.toEntity(user)));
  }

  @Override
  public void delete(User user) {
    userEntityRepository.deleteById(user.getUserId());
  }

  @Override
  public Optional<User> findById(Long id) {
    return userEntityRepository.findById(id)
            .map(UserMapper.INSTANCE::toDomain);
  }
}
