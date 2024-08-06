package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.DeviceEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.DeviceEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, DeviceEntityId> {
  DeviceEntity findByDeviceId(DeviceEntityId deviceEntityId);
}
