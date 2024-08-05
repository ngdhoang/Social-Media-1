package com.GHTK.Social_Network.infrastructure.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyDeviceRequest {
    private String key;

    private String deviceName;

    private String macAddress;

    private String fingerPrint;
}
