package com.GHTK.Social_Network.application.port.output.auth;

public interface DevicePort {
    boolean existDeviceForUser(Long userId, String deviceName, String macAddress, String fingerPrint);

//    Token findByUserIdAndAndDefaultDeviceIsTrue(Long userId);
}
