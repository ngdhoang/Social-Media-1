package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.domain.entity.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUserEmail(String userEmail);

  Boolean existsByUserEmail(String userEmail);

  @Modifying
  @Transactional
  @Query("""
      UPDATE User u
      SET u.password = ?1
      WHERE u.userId = ?2
  """)
  void updatePassword(
          String hashedPassword,
          Long id
  );

  @Modifying
  @Transactional
  @Query("""
      UPDATE User u
      SET u.isProfilePublic = ?1
      WHERE u.userId = ?2
  """)
  int changeStateProfile(Boolean state, Long userId);
}
