package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.requests.AuthRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ChangePasswordRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.RegisterRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.AuthResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.ChangePasswordResponse;

public interface AuthPortInput {
  AuthResponse authenticate(AuthRequest authRequest);

  AuthResponse register(RegisterRequest registerRequest);

  ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest);
}
