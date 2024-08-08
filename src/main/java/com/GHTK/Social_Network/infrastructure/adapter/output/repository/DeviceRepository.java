package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {
  @Query("""
                  select d from DeviceEntity d where d.deviceId = ?1
          """)
  DeviceEntity findByDeviceId(Long deviceEntityId);

  @Query("""
          select d from DeviceEntity d where d.userEntity.userId = ?1 and d.fingerprinting = ?2 and d.deviceInformation = ?3
            """)
  DeviceEntity findByUserIdAndDeviceInfo(Long userId, String fingerprinting, String deviceInformation);
}
