package com.GHTK.Social_Network.domain.repository;

import com.GHTK.Social_Network.domain.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
  User findByUserEmail(String userEmail);

  Boolean existsByUserEmail(String userEmail);

  void updatePassword(String hashedPassword, Long id);

  int changeStateProfile(Boolean state, Long userId);

  int changeAvatar(String url, Long userId);

  List<User> searchUsersByNameOrEmail(String name);

  User save(User user);

  void delete(User user);

  Optional<User> findById(Long id);
}
