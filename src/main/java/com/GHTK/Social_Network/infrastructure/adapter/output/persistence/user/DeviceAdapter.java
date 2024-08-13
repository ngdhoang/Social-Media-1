package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.user;

import com.GHTK.Social_Network.application.port.output.auth.DevicePort;
import com.GHTK.Social_Network.domain.model.user.Device;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.DeviceEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.DeviceRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import com.GHTK.Social_Network.infrastructure.mapper.DeviceMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceAdapter implements DevicePort {
  private final DeviceRepository deviceRepository;
  private final UserRepository userRepository;

  private final DeviceMapperETD deviceMapperETD;

  @Override
  public Device saveDevice(Device device, Long userId) {
    DeviceEntity deviceEntity = deviceMapperETD.toEntity(device);
    UserEntity userEntity = userRepository.findById(userId).orElse(null);
    deviceEntity.setUserEntity(userEntity);
    return deviceMapperETD.toDomain(deviceRepository.save(deviceEntity));
  }

  @Override
  public Device getDevice(String fingerprinting, String deviceInformation, Long userId) {
    return deviceMapperETD.toDomain(deviceRepository.findByUserIdAndDeviceInfo(userId, fingerprinting, deviceInformation));
  }
}
