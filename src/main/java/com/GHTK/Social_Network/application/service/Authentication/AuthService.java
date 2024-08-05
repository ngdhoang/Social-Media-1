package com.GHTK.Social_Network.application.service.Authentication;

import com.GHTK.Social_Network.application.port.input.AuthPortInput;
import com.GHTK.Social_Network.application.port.output.OtpPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.auth.JwtPort;
import com.GHTK.Social_Network.application.port.output.auth.RedisAuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.user.ERole;
import com.GHTK.Social_Network.domain.model.user.Token;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.AuthRedisDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.*;
import com.GHTK.Social_Network.infrastructure.payload.responses.AuthResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthPortInput {
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final AuthPort authPort;
  private final OtpPort otpPort;
  private final JwtPort jwtUtils;
  private final RedisAuthPort redisAuthPort;


  @Override
  public AuthResponse authenticate(AuthRequest authRequest) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
              authRequest.getUserEmail(),
              authRequest.getPassword()
      ));
    } catch (BadCredentialsException e) {
      throw new CustomException("Incorrect username or password", HttpStatus.UNAUTHORIZED);
    }

    var user = authPort.findByEmail(authRequest.getUserEmail())
            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
    UserDetailsImpl userDetails = authPort.getUserDetails(user);
    var jwtToken = jwtUtils.generateToken(userDetails);
    var refreshToken = jwtUtils.generateRefreshToken(userDetails);
    revokeAllUserTokens(userDetails);
    saveUserToken(userDetails, jwtToken);

    return new AuthResponse(jwtToken, refreshToken, user.getRole().toString());
  }

  @Override
  public MessageResponse checkOtpRegister(RegisterRequest registerRequest, int attemptCount, Long timeInterval) {
    validateOtp(registerRequest.getUserEmail(), registerRequest.getOtp(), attemptCount, timeInterval);

    User userSave = createUser(registerRequest);
    userSave = authPort.saveUser(userSave);
    UserDetailsImpl userDetails = authPort.getUserDetails(userSave);
    String jwtToken = jwtUtils.generateToken(userDetails);
    saveUserToken(userDetails, jwtToken);
    return new MessageResponse("Registration successful");
  }

  @Override
  public MessageResponse checkOtpForgotPassword(ForgotPasswordRequest forgotPasswordRequest, int attemptCount, Long timeInterval) {
    validateOtp(forgotPasswordRequest.getUserEmail(), forgotPasswordRequest.getOtp(), attemptCount, timeInterval);

    var user = authPort.findByEmail(forgotPasswordRequest.getUserEmail())
            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
    UserDetailsImpl userDetails = authPort.getUserDetails(user);
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
    validateOtp(authPort.getUserAuth().getUserEmail(), otpRequest.getOtp(), attemptCount, timeInterval);

    authPort.deleteUserByEmail(authPort.getUserAuth().getUserEmail());

    return new MessageResponse("Account deleted");
  }

  @Override
  public MessageResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) throws MessagingException, UnsupportedEncodingException {
    if (!authPort.existsUserByUserEmail(forgotPasswordRequest.getUserEmail())) {
      throw new CustomException("This email doesn't exist", HttpStatus.NOT_FOUND);
    }

    String otp = otpPort.generateOtp();
    saveOtpToRedis(forgotPasswordRequest.getUserEmail(), otp);
    otpPort.sendOtpEmail(forgotPasswordRequest.getUserEmail(), otp);

    return new MessageResponse("OTP sent to email");
  }

  @Override
  public MessageResponse deleteAccount() throws MessagingException, UnsupportedEncodingException {
    String otp = otpPort.generateOtp();
    saveOtpToRedis(authPort.getUserAuth().getUserEmail(), otp);
    otpPort.sendOtpEmail(authPort.getUserAuth().getUserEmail(), otp);

    return new MessageResponse("OTP sent to email");
  }

  @Override
  public AuthResponse refreshToken(String refreshToken) {
    Pair<UserDetailsImpl, String> infoAuth = authPort.refreshToken(refreshToken);
    if (infoAuth == null) {
      throw new CustomException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
    }
    revokeAllUserTokens(infoAuth.getLeft());
    saveUserToken(infoAuth.getLeft(), infoAuth.getRight());
    var user = authPort.findByEmail(infoAuth.getLeft().getUsername())
            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

    return new AuthResponse(infoAuth.getRight(), "", user.getRole().toString());
  }

  @Override
  public MessageResponse register(RegisterRequest registerRequest) throws MessagingException, UnsupportedEncodingException {
    if (authPort.existsUserByUserEmail(registerRequest.getUserEmail())) {
      throw new CustomException("This email already exists", HttpStatus.CONFLICT);
    }

    String otp = otpPort.generateOtp();
    saveOtpToRedis(registerRequest.getUserEmail(), otp);
    otpPort.sendOtpEmail(registerRequest.getUserEmail(), otp);

    return new MessageResponse("OTP sent to email");
  }

  @Override
  public MessageResponse changePassword(ChangePasswordRequest changePasswordRequest) {
    User user = authPort.getUserAuth();

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


    UserDetailsImpl userDetails = authPort.getUserDetails(user);
    revokeAllUserTokens(userDetails);

    return new MessageResponse("Password changed successfully");
  }

  private void validateOtp(String email, String providedOtp, int maxAttempts, long timeInterval) {
    AuthRedisDto authRedisDto = redisAuthPort.findByKey(email);
    if (authRedisDto == null) {
      throw new CustomException("OTP not found", HttpStatus.BAD_REQUEST);
    }

    if (authRedisDto.getCount() >= maxAttempts) {
      redisAuthPort.deleteByKey(email);
      throw new CustomException("Maximum OTP attempts exceeded", HttpStatus.TOO_MANY_REQUESTS);
    }

    if (System.currentTimeMillis() > authRedisDto.getCreateTime().getTime() + timeInterval) {
      redisAuthPort.deleteByKey(email);
      throw new CustomException("OTP has expired", HttpStatus.BAD_REQUEST);
    }

    if (!authRedisDto.getOtp().equals(providedOtp)) {
      authRedisDto.setCount(authRedisDto.getCount() + 1);
      redisAuthPort.createOrUpdate(email, authRedisDto);
      throw new CustomException("Invalid OTP", HttpStatus.BAD_REQUEST);
    }

    redisAuthPort.deleteByKey(email);

  }

  private void saveOtpToRedis(String email, String otp) {
    AuthRedisDto authRedisDto = new AuthRedisDto(null, otp, new Date(), 0);
    redisAuthPort.createOrUpdate(email, authRedisDto);
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
    var token = Token.builder()
            .userId(userDetails.getUserEntity().getUserId())
            .token(jwtToken)
            .tokenType("BEARER")
            .expired(false)
            .revoked(false)
            .build();
    authPort.saveToken(token);
  }

  private void revokeAllUserTokens(UserDetailsImpl userDetails) {
    var validUserTokens = authPort.findAllValidTokenByUser(userDetails);
    if (validUserTokens.isEmpty()) {
      return;
    }
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    authPort.saveAllToken(validUserTokens);
  }
}