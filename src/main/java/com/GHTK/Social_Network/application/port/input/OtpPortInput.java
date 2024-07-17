package com.GHTK.Social_Network.application.port.input;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface OtpPortInput {
  String generateOTP();

  void sendOtpEmail(String email, String otp) throws MessagingException, UnsupportedEncodingException;
}
