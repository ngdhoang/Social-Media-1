package com.ghtk.social_network.domain.service;


import com.ghtk.social_network.application.request.LoginRequest;
import com.ghtk.social_network.application.request.RegisterRequest;
import com.ghtk.social_network.application.responce.AuthenticationResponse;
import com.ghtk.social_network.application.responce.ResponseHandler;
import com.ghtk.social_network.domain.model.ETokenType;
import com.ghtk.social_network.domain.model.Tokens;
import com.ghtk.social_network.domain.model.user.ERole;
import com.ghtk.social_network.domain.model.user.Users;
import com.ghtk.social_network.domain.service.securityservice.UserDetailsImpl;
import com.ghtk.social_network.infrastructure.config.jwt.JwtUtils;
import com.ghtk.social_network.infrastructure.repositories.TokenRepository;
import com.ghtk.social_network.infrastructure.repositories.UserRepository;
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
