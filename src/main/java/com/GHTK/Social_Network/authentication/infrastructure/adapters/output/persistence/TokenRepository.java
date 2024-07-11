package com.GHTK.Social_Network.authentication.infrastructure.adapters.output.persistence;

import com.GHTK.Social_Network.authentication.domain.entities.Tokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Tokens, Long> {
  @Query(value = """
          select t from Tokens t inner join Users u\s
          on t.user.userId = u.userId\s 
          where u.userId = :id and (t.expired = false or t.revoked = false)\s
          """)
  List<Tokens> findAllValidTokenByUser(Long id);
  Optional<Tokens> findByToken(String jwt);
}
