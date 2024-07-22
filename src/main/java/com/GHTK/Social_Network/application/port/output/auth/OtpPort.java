package com.GHTK.Social_Network.application.port.output.auth;

public interface OtpPort {
  void validateOtp(String email, String providedOtp, int maxAttempts, long timeInterval);

  void saveOtp(String email, String otp);

  String generateOtp();

  void sendOtpEmail(String email, String otp);
}
