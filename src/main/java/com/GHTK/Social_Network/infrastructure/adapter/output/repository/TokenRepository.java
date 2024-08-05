package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

  @Query(value =  "select t from TokenEntity t where t.token = ?1")
  Optional<TokenEntity> findByToken(String jwt);

  @Query(value = """
          select CASE WHEN COUNT(t) > 0 THEN true ELSE false END from TokenEntity t inner join UserEntity u\s 
          on t.userEntity.userId = u.userId\s
          where u.userId = :userId\s
          and t.deviceName = :deviceName\s 
          and t.macAddress = :macAddress\s 
          and t.fingerPrint =:fingerPrint
          """)
  boolean existDeviceForUser(Long userId, String deviceName, String macAddress, String fingerPrint);

//  @Query("""
//          select t from TokenEntity t inner join UserEntity u\s
//          on  t.userEntity.userId = u.userId\s
//          where t.userEntity.userId = :id\s
//          """)
//  TokenEntity findByUserIdAndAndDefaultDeviceIsTrue(Long userId);

}
