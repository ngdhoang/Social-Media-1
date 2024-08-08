package com.GHTK.Social_Network.infrastructure.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OTPDeviceResponse {
    private Integer otp;

    private String key;
}
