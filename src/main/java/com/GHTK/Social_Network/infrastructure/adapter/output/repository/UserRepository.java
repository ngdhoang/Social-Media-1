package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByUserEmail(String userEmail);

  Boolean existsByUserEmail(String userEmail);

  @Modifying
  @Transactional
  @Query("""
              update UserEntity u
              set u.oldPassword = u.password, u.password = ?1
              where u.userId = ?2
          """)
  void updatePassword(
          String hashedPassword,
          Long id
  );

  @Modifying
  @Transactional
  @Query("""
          update UserEntity u
          set u.isProfilePublic = ?1
          where u.userId = ?2
          """)
  int changeStateProfile(Boolean state, Long userId);

  @Modifying
  @Transactional
  @Query("""
              update UserEntity u
              set u.avatar = ?1
              where u.userId = ?2
          """)
  int changeAvatar(String url, Long userId);

  @Query("""
              select u from UserEntity u 
              where lower(u.firstName) like lower(concat('%', ?1, '%')) 
              or lower(u.lastName) like lower(concat('%', ?1, '%'))
              or lower(concat(u.firstName, ' ', u.lastName)) like lower(concat('%', ?1, '%'))
              or lower(concat(u.firstName, u.lastName)) like lower(concat('%', ?1, '%'))
              or lower(u.userEmail) like lower(concat('%', ?1, '%'))
          """)
  List<UserEntity> searchUsersByNameOrEmail(String name);

//  Optional<UserEntity> findByPosts(PostEntity postEntity);
}
