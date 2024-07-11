package com.GHTK.Social_Network.authentication.infrastructure.adapters.output.persistence;

import com.GHTK.Social_Network.authentication.domain.entities.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
  Optional<Users> findByUserEmail(String userEmail);

  Boolean existsByUserEmail(String userEmail);
}
