package com.ghtk.social_network.infrastructure.repositories;

import com.ghtk.social_network.infrastructure.entity.user.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UsersEntity, Long> {
  Optional<UsersEntity> findByUserEmail(String userEmail);

  Boolean existsByUserEmail(String userEmail);


}
