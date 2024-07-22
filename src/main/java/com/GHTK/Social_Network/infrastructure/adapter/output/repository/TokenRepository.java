package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
  @Query(value = """
          select t from TokenEntity t inner join UserEntity u\s
          on t.userEntity.userId = u.userId\s
          where u.userId = :id and (t.expired = false or t.revoked = false)\s
          """)
  List<TokenEntity> findAllValidTokenByUser(Long id);

  Optional<TokenEntity> findByToken(String jwt);
}
