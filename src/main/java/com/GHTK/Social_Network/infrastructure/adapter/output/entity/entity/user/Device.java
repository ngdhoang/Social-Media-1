package com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user;

import jakarta.persistence.*;

@Entity
public class Device {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long deviceId;

  // Loại thiết bị (ví dụ: điện thoại, máy tính bảng, máy tính bàn).
  private String deviceType;

  // Tên thiết bị (ví dụ: "iPhone 12", "Samsung Galaxy S21").
  private String deviceName;

  // Hệ điều hành của thiết bị (ví dụ: iOS, Android, Windows).
  private String operatingSystem;

  // Phiên bản hệ điều hành (ví dụ: iOS 14.4, Android 11).
  private String osVersion;

  // Mô hình thiết bị (ví dụ: iPhone 12 Pro, Galaxy S21 Ultra).
  private String model;

  // Địa chỉ IP
  private String ipAddress;

  // Thông tin về trình duyệt (nếu là một thiết bị truy cập web).
  private String browserInformation;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity userEntity;
}
