package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.requests.AuthRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ChangePasswordRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.RegisterRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.AuthResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface AuthPortInput {
  AuthResponse authenticate(AuthRequest authRequest);

  MessageResponse register(RegisterRequest registerRequest) throws MessagingException, UnsupportedEncodingException;

  MessageResponse changePassword(ChangePasswordRequest changePasswordRequest);

  AuthResponse checkOtp(RegisterRequest registerRequest, int attemptCount, Long timeInterval);
}
