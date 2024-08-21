package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "device")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long deviceId;

  private String fingerprinting;

  private String deviceInformation;

  private EDeviceTypeEntity deviceType;

  private Instant localDate;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;

  @PrePersist
  public void onPersist(){
    this.localDate = Instant.now();
  }
}