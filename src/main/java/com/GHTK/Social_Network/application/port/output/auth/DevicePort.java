package com.GHTK.Social_Network.application.port.output.auth;

import com.GHTK.Social_Network.domain.model.user.Device;

public interface DevicePort {
  Device saveDevice(Device device, Long userId);

  Device getDevice(String fingerprinting, String deviceInformation, Long userId);
}
