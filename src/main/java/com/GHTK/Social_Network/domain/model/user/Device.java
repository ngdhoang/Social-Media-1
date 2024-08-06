package com.GHTK.Social_Network.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Device {
  private Long deviceId;

  private String fingerprinting;

  private String deviceInformation;

  private EDeviceType deviceType;

  private LocalDate localDate;

  private Long userId;

  public Device(String fingerprinting, String deviceInformation, EDeviceType deviceType, LocalDate localDate, Long userId) {
    this.fingerprinting = fingerprinting;
    this.deviceInformation = deviceInformation;
    this.deviceType = deviceType;
    this.localDate = localDate;
    this.userId = userId;
  }

  public Device(String fingerprinting, String deviceInformation, EDeviceType deviceType, LocalDate localDate) {
    this.fingerprinting = fingerprinting;
    this.deviceInformation = deviceInformation;
    this.deviceType = deviceType;
    this.localDate = localDate;
  }
}