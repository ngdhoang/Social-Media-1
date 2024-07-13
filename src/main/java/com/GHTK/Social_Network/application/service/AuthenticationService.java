package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.AuthenticationPortInput;
import com.GHTK.Social_Network.application.port.output.AuthenticationPort;
import com.GHTK.Social_Network.domain.entity.user.ERole;
import com.GHTK.Social_Network.domain.entity.user.Token;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.jwt.JwtUtils;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import com.GHTK.Social_Network.infrastructure.adapter.output.persistence.AuthenticationRepositoryPortImpl;
import com.GHTK.Social_Network.infrastructure.payload.requests.AuthenticationRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.RegisterRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationPortInput {
  private final AuthenticationManager authenticationManager;

  private final JwtUtils jwtUtils;

  private final PasswordEncoder passwordEncoder;

  private final AuthenticationPort authenticationRepositoryPort;

  @Override
  public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            authenticationRequest.getUserEmail(),
            authenticationRequest.getPassword()
    ));
    var user = authenticationRepositoryPort.findByEmail(authenticationRequest.getUserEmail()).orElseThrow();
    UserDetailsImpl userDetails = new UserDetailsImpl(user);
    var jwtToken = jwtUtils.generateToken(userDetails);
    var refreshToken = jwtUtils.generateRefreshToken(userDetails);
    revokeAllUserTokens(userDetails);
    saveUserToken(userDetails, jwtToken);
    return new AuthenticationResponse(
            jwtToken,
            refreshToken,
            user.getRole().toString()
    );
  }

  @Override
  public AuthenticationResponse register(RegisterRequest registerRequest) {
    User users = new User(
            registerRequest.getFirstName(),
            registerRequest.getLastName(),
            registerRequest.getUserEmail(),
            passwordEncoder.encode(registerRequest.getPassword())
    );
    users.setRole(ERole.USER);
    UserDetailsImpl userDetails = new UserDetailsImpl(users);
    authenticationRepositoryPort.save(users);
    var jwtToken = jwtUtils.generateToken(userDetails);
    var refreshToken = jwtUtils.generateRefreshToken(userDetails);
    saveUserToken(userDetails, jwtToken);
    return new AuthenticationResponse(
            jwtToken,
            refreshToken,
            users.getRole().toString()
    );
  }

  private void saveUserToken(UserDetailsImpl userDetails, String jwtToken) {
    var token = Token.builder()
            .user(userDetails.getUser())
            .token(jwtToken)
            .tokenType("BEARER")
            .expired(false)
            .revoked(false)
            .build();
    authenticationRepositoryPort.save(token);
  }

  private void revokeAllUserTokens(UserDetailsImpl userDetails) {
    var validUserToken = authenticationRepositoryPort.findAllValidTokenByUser(userDetails.getUser().getUserId());
    if (validUserToken.isEmpty())
      return;
    validUserToken.forEach(
            token -> {
              token.setExpired(true);
              token.setRevoked(true);
            }
    );
    authenticationRepositoryPort.saveAll(validUserToken);
  }
}
