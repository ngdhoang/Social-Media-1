package com.ghtk.social_network.infrastructure.repositories;

import com.ghtk.social_network.infrastructure.entity.TokensEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokensEntity, Long> {
  @Query(value = """
          select t from TokensEntity t inner join UsersEntity u\s
          on t.user.userId = u.userId\s 
          where u.userId = :id and (t.expired = false or t.revoked = false)\s
          """)
  List<TokensEntity> findAllValidTokenByUser(Long id);
  Optional<TokensEntity> findByToken(String jwt);
}
