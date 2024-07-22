package com.GHTK.Social_Network.application.service.Authentication;

import com.GHTK.Social_Network.application.port.input.AuthPortInput;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.auth.JwtPort;
import com.GHTK.Social_Network.application.port.output.auth.OtpPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.user.ERole;
import com.GHTK.Social_Network.domain.model.user.Token;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.MapperEntity.UserMapper;
import com.GHTK.Social_Network.infrastructure.payload.requests.*;
import com.GHTK.Social_Network.infrastructure.payload.responses.AuthResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.security.service.UserDetailsImpl;
import jakarta.mail.MessagingException;
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

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthPortInput {
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final AuthPort authPort;
  private final OtpPort otpPort;
  private final JwtPort jwtUtils;

  private User getUserAuth() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username;

    if (principal instanceof UserDetails) {
      username = ((UserDetails) principal).getUsername();
    } else if (principal instanceof String) {
      username = (String) principal;
    } else {
      throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }

    return authPort.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid token"));
  }

  @Override
  public AuthResponse authenticate(AuthRequest authRequest) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            authRequest.getUserEmail(),
            authRequest.getPassword()
    ));

    var user = authPort.findByEmail(authRequest.getUserEmail())
            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
    UserDetailsImpl userDetails = new UserDetailsImpl(UserMapper.INSTANCE.toEntity(user));
    var jwtToken = jwtUtils.generateToken(userDetails);
    var refreshToken = jwtUtils.generateRefreshToken(userDetails);
    revokeAllUserTokens(userDetails);
    saveUserToken(userDetails, jwtToken);

    return new AuthResponse(jwtToken, refreshToken, user.getRole().toString());
  }

  @Override
  public MessageResponse checkOtpRegister(RegisterRequest registerRequest, int attemptCount, Long timeInterval) {
    otpPort.validateOtp(registerRequest.getUserEmail(), registerRequest.getOtp(), attemptCount, timeInterval);

    User user = createUser(registerRequest);
    authPort.saveUser(user);
    UserDetailsImpl userDetails = new UserDetailsImpl(UserMapper.INSTANCE.toEntity(user));
    String jwtToken = jwtUtils.generateToken(userDetails);
    saveUserToken(userDetails, jwtToken);

    return new MessageResponse("Registration successful");
  }

  @Override
  public MessageResponse checkOtpForgotPassword(ForgotPasswordRequest forgotPasswordRequest, int attemptCount, Long timeInterval) {
    otpPort.validateOtp(forgotPasswordRequest.getUserEmail(), forgotPasswordRequest.getOtp(), attemptCount, timeInterval);

    var user = authPort.findByEmail(forgotPasswordRequest.getUserEmail())
            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
    UserDetailsImpl userDetails = new UserDetailsImpl(UserMapper.INSTANCE.toEntity(user));
    revokeAllUserTokens(userDetails);

    if (passwordEncoder.matches(forgotPasswordRequest.getNewPassword(), user.getPassword())) {
      throw new CustomException("Old password and new password must be different", HttpStatus.CONFLICT);
    }

    String encodeNewPassword = passwordEncoder.encode(forgotPasswordRequest.getNewPassword());
    authPort.changePassword(encodeNewPassword, user.getUserId());

    return new MessageResponse("Password changed");
  }

  @Override
  public MessageResponse checkOtpDeleteAccount(OTPRequest otpRequest, int attemptCount, Long timeInterval) {
    otpPort.validateOtp(getUserAuth().getUserEmail(), otpRequest.getOtp(), attemptCount, timeInterval);

    authPort.deleteUserByEmail(getUserAuth().getUserEmail());

    return new MessageResponse("Account deleted");
  }

  @Override
  public MessageResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) throws MessagingException, UnsupportedEncodingException {
    if (!authPort.existsUserByUserEmail(forgotPasswordRequest.getUserEmail())) {
      throw new CustomException("This email doesn't exist", HttpStatus.NOT_FOUND);
    }

    String otp = otpPort.generateOtp();
    otpPort.saveOtp(forgotPasswordRequest.getUserEmail(), otp);
    otpPort.sendOtpEmail(forgotPasswordRequest.getUserEmail(), otp);

    return new MessageResponse("OTP sent to email");
  }

  @Override
  public MessageResponse deleteAccount() throws MessagingException, UnsupportedEncodingException {
    String otp = otpPort.generateOtp();
    otpPort.saveOtp(getUserAuth().getUserEmail(), otp);
    otpPort.sendOtpEmail(getUserAuth().getUserEmail(), otp);

    return new MessageResponse("OTP sent to email");
  }

  @Override
  public MessageResponse register(RegisterRequest registerRequest) throws MessagingException, UnsupportedEncodingException {
    if (authPort.existsUserByUserEmail(registerRequest.getUserEmail())) {
      throw new CustomException("This email already exists", HttpStatus.CONFLICT);
    }

    String otp = otpPort.generateOtp();
    otpPort.saveOtp(registerRequest.getUserEmail(), otp);
    otpPort.sendOtpEmail(registerRequest.getUserEmail(), otp);

    return new MessageResponse("OTP sent to email");
  }

  @Override
  public MessageResponse changePassword(ChangePasswordRequest changePasswordRequest) {
    User user = getUserAuth();

    if (changePasswordRequest.getOldPassword().equals(changePasswordRequest.getNewPassword())) {
      throw new CustomException("Old password and new password must be different", HttpStatus.CONFLICT);
    }

    if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
      throw new CustomException("Old password is incorrect", HttpStatus.BAD_REQUEST);
    }

    if (user.getOldPassword() != null && passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getOldPassword())) {
      throw new CustomException("Password has already been used before", HttpStatus.BAD_REQUEST);
    }

    String encodeNewPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
    authPort.changePassword(encodeNewPassword, user.getUserId());

    UserDetailsImpl userDetails = new UserDetailsImpl(UserMapper.INSTANCE.toEntity(user));
    revokeAllUserTokens(userDetails);

    return new MessageResponse("Password changed successfully");
  }

  private User createUser(RegisterRequest registerRequest) {
    User user = new User(
            registerRequest.getFirstName(),
            registerRequest.getLastName(),
            registerRequest.getUserEmail(),
            passwordEncoder.encode(registerRequest.getPassword())
    );
    user.setRole(ERole.USER);
    return user;
  }

  private void saveUserToken(UserDetailsImpl userDetails, String jwtToken) {
    Token token = Token.builder()
            .userId(userDetails.getUserEntity().getUserId())
            .token(jwtToken)
            .tokenType("BEARER")
            .expired(false)
            .revoked(false)
            .build();
    authPort.saveToken(token);
  }

  private void revokeAllUserTokens(UserDetailsImpl userDetails) {
    List<Token> tokens = authPort.findAllValidTokenByUser(userDetails.getUserEntity().getUserId());
    tokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    authPort.saveAll(tokens);
  }
}
