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
  private String fingerprinting;

  private String deviceInformation;

  private EDeviceType deviceType;

  private LocalDate localDate;

  private Long userId;
}