package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.requests.AuthenticationRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ChangePasswordRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.RegisterRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.AuthenticationResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.ChangePasswordResponse;

public interface AuthenticationPortInput {
  AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);

  AuthenticationResponse register(RegisterRequest registerRequest);

  ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest);
}
