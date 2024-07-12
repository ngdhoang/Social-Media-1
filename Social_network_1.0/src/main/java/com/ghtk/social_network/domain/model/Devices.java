package com.ghtk.social_network.domain.model;

import com.ghtk.social_network.domain.model.user.Users;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class Devices {

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


}
