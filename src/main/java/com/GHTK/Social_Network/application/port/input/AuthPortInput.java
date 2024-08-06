package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.requests.*;
import com.GHTK.Social_Network.infrastructure.payload.responses.AuthResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface AuthPortInput {
  int MAX_COUNT_OTP = 3;

  AuthResponse authenticate(AuthRequest authRequest, String fingerprinting);

  MessageResponse register(RegisterRequest registerRequest) throws MessagingException, UnsupportedEncodingException;

  MessageResponse changePassword(ChangePasswordRequest changePasswordRequest);

  MessageResponse checkOtpRegister(RegisterRequest registerRequest, int attemptCount, Long timeInterval, String fingerprinting);

  MessageResponse checkOtpForgotPassword(ForgotPasswordRequest forgotPasswordRequest, int attemptCount, Long timeInterval);

  MessageResponse checkOtpDeleteAccount(OTPRequest otpRequest, int attemptCount, Long timeInterval);

  MessageResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) throws MessagingException, UnsupportedEncodingException;

  MessageResponse deleteAccount() throws MessagingException, UnsupportedEncodingException;

  AuthResponse refreshToken(String refreshTokenRequest, String fingerprinting);
}
