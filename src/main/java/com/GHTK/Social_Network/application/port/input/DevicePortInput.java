package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.requests.AuthRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.OTPRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.VerifyDeviceRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface DevicePortInput {
   boolean checkDevice(String email,Long userId,String deviceName,String macAddress,String fingerPrint);

   MessageResponse checkOtpLoginNewDevice(String otp, VerifyDeviceRequest verifyDeviceRequest);
}
