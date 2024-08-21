package com.GHTK.Social_Network.domain.model.user;

import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Device {
  private Long deviceId;

  private String fingerprinting;

  private String deviceInformation;

  private EDeviceType deviceType;

  private Instant localDate;

  private Long userId;

  public Device(String fingerprinting, String deviceInformation, EDeviceType deviceType, Long userId) {
    this.fingerprinting = fingerprinting;
    this.deviceInformation = deviceInformation;
    this.deviceType = deviceType;
    this.userId = userId;
  }

  public Device(String fingerprinting, String deviceInformation, EDeviceType deviceType) {
    this.fingerprinting = fingerprinting;
    this.deviceInformation = deviceInformation;
    this.deviceType = deviceType;
  }

  @PrePersist
  public void prePersist() {
    localDate = Instant.now();
  }
}