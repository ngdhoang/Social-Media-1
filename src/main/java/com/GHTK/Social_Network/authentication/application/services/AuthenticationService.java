package com.GHTK.Social_Network.authentication.application.services;

import com.GHTK.Social_Network.authentication.application.payloads.requests.LoginRequest;
import com.GHTK.Social_Network.authentication.application.payloads.requests.RegisterRequest;
import com.GHTK.Social_Network.authentication.application.payloads.responses.AuthenticationResponse;
import com.GHTK.Social_Network.authentication.application.payloads.responses.ResponseHandler;
import com.GHTK.Social_Network.authentication.domain.entities.ETokenType;
import com.GHTK.Social_Network.authentication.domain.entities.Tokens;
import com.GHTK.Social_Network.authentication.domain.entities.user.ERole;
import com.GHTK.Social_Network.authentication.domain.entities.user.Users;
import com.GHTK.Social_Network.authentication.infrastructure.adapters.output.persistence.TokenRepository;
import com.GHTK.Social_Network.authentication.infrastructure.adapters.output.persistence.UserRepository;
import com.GHTK.Social_Network.authentication.infrastructure.adapters.output.security.jwt.JwtUtils;
import com.GHTK.Social_Network.authentication.infrastructure.adapters.output.security.sevices.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final AuthenticationManager authenticationManager;

  private final UserRepository userRepository;

  private final TokenRepository tokenRepository;

  private final JwtUtils jwtUtils;

  private final PasswordEncoder passwordEncoder;

  public AuthenticationResponse logIn(LoginRequest logInRequest) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            logInRequest.getUserEmail(),
            logInRequest.getPassword()
    ));
    var user = userRepository.findByUserEmail(logInRequest.getUserEmail()).orElseThrow();
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

  public Object signUp(RegisterRequest signUpRequest) {
    if (userRepository.existsByUserEmail(signUpRequest.getUserEmail())) {
      return ResponseHandler.generateErrorResponse(new RuntimeException("This Gmail already exists"));
    }
    Users users = new Users(
            signUpRequest.getFirstName(),
            signUpRequest.getLastName(),
            signUpRequest.getUserEmail(),
            passwordEncoder.encode(signUpRequest.getPassword())
    );
    users.setRole(ERole.USER);
    UserDetailsImpl userDetails = new UserDetailsImpl(users);
    userRepository.save(users);
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
    var token = Tokens.builder()
            .user(userDetails.getUser())
            .token(jwtToken)
            .tokenType(ETokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(UserDetailsImpl userDetails) {
    var validUserToken = tokenRepository.findAllValidTokenByUser(userDetails.getUser().getUserId());
    if (validUserToken.isEmpty())
      return;
    validUserToken.forEach(
            token -> {
              token.setExpired(true);
              token.setRevoked(true);
            }
    );
    tokenRepository.saveAll(validUserToken);
  }
}
