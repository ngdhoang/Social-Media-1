package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.AuthPortInput;
import com.GHTK.Social_Network.application.port.output.AuthPort;
import com.GHTK.Social_Network.domain.entity.user.ERole;
import com.GHTK.Social_Network.domain.entity.user.Token;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.jwt.JwtUtils;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import com.GHTK.Social_Network.infrastructure.exception.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.requests.AuthRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ChangePasswordRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.RegisterRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.AuthResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.ChangePasswordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthPortInput {
  private final AuthenticationManager authenticationManager;

  private final JwtUtils jwtUtils;

  private final PasswordEncoder passwordEncoder;

  private final AuthPort authenticationRepositoryPort;

  private User getUserFromRequest() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username;

    if (principal instanceof UserDetails) {
      username = ((UserDetails) principal).getUsername();
    } else if (principal instanceof String) {
      username = (String) principal;
    } else {
      throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }

    return authenticationRepositoryPort.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid token"));
  }

  @Override
  public AuthResponse authenticate(AuthRequest authRequest) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            authRequest.getUserEmail(),
            authRequest.getPassword()
    ));
    var user = authenticationRepositoryPort.findByEmail(authRequest.getUserEmail()).orElseThrow();
    UserDetailsImpl userDetails = new UserDetailsImpl(user);
    var jwtToken = jwtUtils.generateToken(userDetails);
    var refreshToken = jwtUtils.generateRefreshToken(userDetails);
    revokeAllUserTokens(userDetails);
    saveUserToken(userDetails, jwtToken);
    return new AuthResponse(
            jwtToken,
            refreshToken,
            user.getRole().toString()
    );
  }

  @Override
  public AuthResponse register(RegisterRequest registerRequest) {
    if (authenticationRepositoryPort.existsUserByUserEmail(registerRequest.getUserEmail())) {
      throw new CustomException("This Gmail already exists", HttpStatus.BAD_GATEWAY);
    }
    User users = new User(
            registerRequest.getFirstName(),
            registerRequest.getLastName(),
            registerRequest.getUserEmail(),
            passwordEncoder.encode(registerRequest.getPassword())
    );
    users.setRole(ERole.USER);
    UserDetailsImpl userDetails = new UserDetailsImpl(users);
    authenticationRepositoryPort.saveUser(users);
    var jwtToken = jwtUtils.generateToken(userDetails);
    var refreshToken = jwtUtils.generateRefreshToken(userDetails);
    saveUserToken(userDetails, jwtToken);
    return new AuthResponse(
            jwtToken,
            refreshToken,
            users.getRole().toString()
    );
  }

  @Override
  public ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest) {
    User user = getUserFromRequest();

    if (changePasswordRequest.getOldPassword().equals(changePasswordRequest.getNewPassword()))
      throw new CustomException("Old password and new password must be different", HttpStatus.CONFLICT);

    if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
      throw new CustomException("Old password is incorrect", HttpStatus.BAD_REQUEST);

    if (!passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getOldPassword()))
      throw new CustomException("Password has already been used before", HttpStatus.BAD_REQUEST);

    String encodeNewPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
    authenticationRepositoryPort.changePassword(encodeNewPassword, user.getUserId());
    return new ChangePasswordResponse("Password changed successfully");
  }


  private void saveUserToken(UserDetailsImpl userDetails, String jwtToken) {
    var token = Token.builder()
            .user(userDetails.getUser())
            .token(jwtToken)
            .tokenType("BEARER")
            .expired(false)
            .revoked(false)
            .build();
    authenticationRepositoryPort.saveToken(token);
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
