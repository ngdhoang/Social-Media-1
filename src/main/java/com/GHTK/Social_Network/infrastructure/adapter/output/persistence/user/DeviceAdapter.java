package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.user;

import com.GHTK.Social_Network.application.port.output.auth.DevicePort;
import com.GHTK.Social_Network.domain.model.user.Device;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.DeviceEntityId;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.DeviceRepository;
import com.GHTK.Social_Network.infrastructure.mapper.DeviceMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceAdapter implements DevicePort {
  private final DeviceRepository deviceRepository;
  private final DeviceMapperETD deviceMapperETD;

  @Override
  public Device saveDevice(Device device) {
    return deviceMapperETD.toDomain(deviceRepository.save(deviceMapperETD.toEntity(device)));
  }

  @Override
  public Device getDevice(String fingerprinting, String deviceInformation) {
    return deviceMapperETD.toDomain(deviceRepository.findByDeviceId(
            new DeviceEntityId(
                    fingerprinting,
                    deviceInformation
            )
    ));
  }
}
