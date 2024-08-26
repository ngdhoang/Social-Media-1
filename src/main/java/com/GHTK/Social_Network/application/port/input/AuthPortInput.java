package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.requests.auth.*;
import com.GHTK.Social_Network.infrastructure.payload.responses.AuthResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface AuthPortInput {
  int MAX_COUNT_OTP = 3;

  Object authenticate(AuthRequest authRequest, String userAgent, String fingerprinting);

  MessageResponse register(RegisterRequest registerRequest, String userAgent, String fingerprinting) throws MessagingException, UnsupportedEncodingException;

  MessageResponse changePassword(ChangePasswordRequest changePasswordRequest);

  MessageResponse checkOtpRegister(RegisterRequest registerRequest, String userAgent, String fingerprinting, int attemptCount, Long timeInterval);

  MessageResponse checkOtpForgotPassword(ForgotPasswordRequest forgotPasswordRequest, int attemptCount, Long timeInterval);

  MessageResponse checkOtpDeleteAccount(OTPRequest otpRequest, int attemptCount, Long timeInterval);

  MessageResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) throws MessagingException, UnsupportedEncodingException;

  MessageResponse deleteAccount() throws MessagingException, UnsupportedEncodingException;

  AuthResponse refreshToken(String refreshTokenRequest, String fingerprinting);

  Object checkSuccessDevice(String key, String userAgent, String fingerprinting);
}
