package com.GHTK.Social_Network.application.port.input.post;

import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;

import java.util.Map;

public interface DevicePortInput {
  MessageResponse checkDeviceOtp(int otp);

  Map<String, Object> generateOtp(int cnt);
}
