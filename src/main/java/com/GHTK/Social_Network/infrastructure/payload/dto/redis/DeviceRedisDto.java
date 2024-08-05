package com.GHTK.Social_Network.infrastructure.payload.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceRedisDto {

    private String deviceName;

    private String macAddress;

    private String fingerPrint;

    private boolean defaultDevice;
//
//    private Date createTime;
//
//    private int count;

}
