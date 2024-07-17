package com.GHTK.Social_Network.application.port.input;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

public interface OtpPortInput {
  String generateOTP();

  CompletableFuture<Void> sendOtpEmail(String email, String otp) throws MessagingException, UnsupportedEncodingException;
}
