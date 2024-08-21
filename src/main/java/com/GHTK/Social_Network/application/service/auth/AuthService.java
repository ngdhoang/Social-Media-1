package com.GHTK.Social_Network.application.service.auth;

import com.GHTK.Social_Network.application.port.input.AuthPortInput;
import com.GHTK.Social_Network.application.port.input.post.DevicePortInput;
import com.GHTK.Social_Network.application.port.output.OtpPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.auth.DevicePort;
import com.GHTK.Social_Network.application.port.output.auth.JwtPort;
import com.GHTK.Social_Network.application.port.output.auth.redis.RedisAuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.event.user.CreateUserEvent;
import com.GHTK.Social_Network.domain.event.user.RemoveUserEvent;
import com.GHTK.Social_Network.domain.model.user.Device;
import com.GHTK.Social_Network.domain.model.user.EDeviceType;
import com.GHTK.Social_Network.domain.model.user.ERole;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import com.GHTK.Social_Network.infrastructure.payload.dto.AccessTokenDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.AuthRedisDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.auth.*;
import com.GHTK.Social_Network.infrastructure.payload.responses.AuthResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthPortInput {
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;

  private final AuthPort authPort;
  private final OtpPort otpPort;
  private final JwtPort jwtUtils;
  private final DevicePort devicePort;
  private final DevicePortInput devicePortInput;
  private final ApplicationEventPublisher applicationEventPublisher;

  private final RedisAuthPort redisAuthPort;

  @Override
  public Object authenticate(AuthRequest authRequest, String userAgent, String fingerprinting) {
    authenticateUser(authRequest.getUserEmail(), authRequest.getPassword());
    User user = findUserByEmail(authRequest.getUserEmail());

    if (isNewDevice(fingerprinting, userAgent, user.getUserId())) {
      return handleNewDevice(user.getUserEmail(), fingerprinting, userAgent);
    }

    return authenticationWithDeviceTrust(user, fingerprinting);
  }

  @Override
  public Object checkSuccessDevice(String key, String userAgent, String fingerprinting) {
    String newKey = key + "_" + fingerprinting + "_" + userAgent + RedisAuthPort.DEVICE_CHECK_TAIL;
    if (!redisAuthPort.existsByKey(newKey)) {
      return new MessageResponse("Nothing...");
    }

    AuthRedisDto authRedisDto = redisAuthPort.findByKey(newKey);
    String email = authRedisDto.getRegisterRequest().getUserEmail();
    User user = findUserByEmail(email);
    redisAuthPort.deleteByKey(newKey);
    return authenticationWithDeviceTrust(user, fingerprinting);
  }

  @Override
  public MessageResponse checkOtpRegister(RegisterRequest registerRequest, String userAgent, String fingerprinting, int attemptCount, Long timeInterval) {
    validateOtp(registerRequest.getUserEmail(), registerRequest.getOtp(), attemptCount, timeInterval);

    User user = createAndSaveUser(registerRequest);
    saveNewDevice(fingerprinting, userAgent, user);

    applicationEventPublisher.publishEvent(new CreateUserEvent(user));

    return new MessageResponse("Registration successful");
  }

  @Override
  public MessageResponse checkOtpForgotPassword(ForgotPasswordRequest forgotPasswordRequest, int attemptCount, Long timeInterval) {
    validateOtp(forgotPasswordRequest.getUserEmail(), forgotPasswordRequest.getOtp(), attemptCount, timeInterval);

    User user = findUserByEmail(forgotPasswordRequest.getUserEmail());
    validateNewPassword(forgotPasswordRequest.getNewPassword(), user);

    String encodeNewPassword = passwordEncoder.encode(forgotPasswordRequest.getNewPassword());
    authPort.changePassword(encodeNewPassword, user.getUserId());

    revokeAllUserTokens(authPort.getUserDetails(user));

    return new MessageResponse("Password changed");
  }

  @Override
  public MessageResponse checkOtpDeleteAccount(OTPRequest otpRequest, int attemptCount, Long timeInterval) {
    String userEmail = authPort.getUserAuth().getUserEmail();
    validateOtp(userEmail, otpRequest.getOtp(), attemptCount, timeInterval);

    authPort.deleteUserByEmail(userEmail);
    applicationEventPublisher.publishEvent(new RemoveUserEvent(userEmail));

    return new MessageResponse("Account deleted");
  }

  @Override
  public MessageResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
    if (!authPort.existsUserByUserEmail(forgotPasswordRequest.getUserEmail())) {
      throw new CustomException("This email doesn't exist", HttpStatus.NOT_FOUND);
    }

    String otp = generateAndSendOtp(forgotPasswordRequest.getUserEmail());

    return new MessageResponse("OTP sent to email");
  }

  @Override
  public MessageResponse deleteAccount() {
    String userEmail = authPort.getUserAuth().getUserEmail();
    String otp = generateAndSendOtp(userEmail);

    return new MessageResponse("OTP sent to email");
  }

  @Override
  public AuthResponse refreshToken(String refreshToken, String fingerprinting) {
    Pair<UserDetailsImpl, String> infoAuth = authPort.refreshToken(refreshToken, fingerprinting);
    if (infoAuth == null) {
      throw new CustomException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
    }

    UserDetailsImpl userDetails = infoAuth.getLeft();
    String newToken = infoAuth.getRight();

    revokeAllUserTokens(userDetails);
    saveUserToken(userDetails, newToken, fingerprinting);

    User user = findUserByEmail(userDetails.getUsername());

    return new AuthResponse(newToken, "", user.getRole().toString());
  }

  @Override
  public MessageResponse register(RegisterRequest registerRequest, String userAgent, String fingerprinting) {
    if (authPort.existsUserByUserEmail(registerRequest.getUserEmail())) {
      throw new CustomException("This email already exists", HttpStatus.CONFLICT);
    }

    String otp = otpPort.generateOtp();
    saveOtpToRedis(registerRequest, fingerprinting, userAgent, otp);
    otpPort.sendOtpEmail(registerRequest.getUserEmail(), otp);

    return new MessageResponse("OTP sent to email");
  }

  @Override
  public MessageResponse changePassword(ChangePasswordRequest changePasswordRequest) {
    User user = authPort.getUserAuth();

    validatePasswordChange(changePasswordRequest, user);

    String encodeNewPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
    authPort.changePassword(encodeNewPassword, user.getUserId());

    revokeAllUserTokens(authPort.getUserDetails(user));

    return new MessageResponse("Password changed successfully");
  }

  private void authenticateUser(String email, String password) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    } catch (BadCredentialsException e) {
      throw new CustomException("Incorrect username or password", HttpStatus.UNAUTHORIZED);
    }
  }

  private User findUserByEmail(String email) {
    return authPort.findByEmail(email)
            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
  }

  private boolean isNewDevice(String fingerprinting, String userAgent, Long userId) {
    return devicePort.getDevice(fingerprinting, userAgent, userId) == null;
  }

  private MessageResponse handleNewDevice(String userEmail, String fingerprinting, String userAgent) {
    Map<String, Object> otpData = devicePortInput.generateOtp(3);
    Integer chosenNumber = (Integer) otpData.get("selectedNumber");
    List<String> otpList = ((List<Integer>) otpData.get("generatedNumbers")).stream()
            .map(Object::toString)
            .collect(Collectors.toList());

    redisAuthPort.createOrUpdate(chosenNumber + RedisAuthPort.DEVICE_TAIL + userEmail,
            AuthRedisDto.builder()
                    .fingerprinting(fingerprinting)
                    .userAgent(userAgent)
                    .otp(otpList)
                    .key(UUID.randomUUID().toString())
                    .build());

    return new MessageResponse("OTP: " + chosenNumber);
  }

  private AuthResponse authenticationWithDeviceTrust(User user, String fingerprinting) {
    UserDetailsImpl userDetails = authPort.getUserDetails(user);
    String jwtToken = jwtUtils.generateToken(userDetails, fingerprinting);
    String refreshToken = jwtUtils.generateRefreshToken(userDetails, fingerprinting);

    revokeAllUserTokens(userDetails);
    saveUserToken(userDetails, jwtToken, fingerprinting);
    saveUserRefreshToken(refreshToken, fingerprinting, userDetails);

    return new AuthResponse(jwtToken, refreshToken, user.getRole().toString());
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

    if (System.currentTimeMillis() > authRedisDto.getCreateAt().getTime() + timeInterval) {
      redisAuthPort.deleteByKey(email);
      throw new CustomException("OTP has expired", HttpStatus.BAD_REQUEST);
    }

    if (!authRedisDto.getOtp().get(0).equals(providedOtp)) {
      authRedisDto.setCount(authRedisDto.getCount() + 1);
      redisAuthPort.createOrUpdate(email, authRedisDto);
      throw new CustomException("Invalid OTP", HttpStatus.BAD_REQUEST);
    }

    redisAuthPort.deleteByKey(email);
  }

  private void saveOtpToRedis(RegisterRequest registerRequest, String fingerprinting, String userAgent, String otp) {
    AuthRedisDto authRedisDto = new AuthRedisDto(registerRequest, fingerprinting, userAgent, List.of(otp), new Date(), 0);
    redisAuthPort.createOrUpdate(registerRequest.getUserEmail(), authRedisDto);
  }

  private void saveOtpToRedis(String userEmail, String otp) {
    AuthRedisDto authRedisDto = new AuthRedisDto(List.of(otp), new Date(), 0);
    redisAuthPort.createOrUpdate(userEmail, authRedisDto);
  }

  private User createAndSaveUser(RegisterRequest registerRequest) {
    User user = new User(
            registerRequest.getFirstName(),
            registerRequest.getLastName(),
            registerRequest.getUserEmail(),
            passwordEncoder.encode(registerRequest.getPassword())
    );
    user.setRole(ERole.USER);
    return authPort.saveUser(user);
  }

  private void saveNewDevice(String fingerprinting, String userAgent, User user) {
    Device newDevice = new Device(
            fingerprinting,
            userAgent,
            EDeviceType.DEFAULT,
            user.getUserId()
    );
    devicePort.saveDevice(newDevice, user.getUserId());
  }

  private void validateNewPassword(String newPassword, User user) {
    if (passwordEncoder.matches(newPassword, user.getPassword())) {
      throw new CustomException("Old password and new password must be different", HttpStatus.CONFLICT);
    }
  }

  private String generateAndSendOtp(String email) {
    String otp = otpPort.generateOtp();
    saveOtpToRedis(email, otp);
    otpPort.sendOtpEmail(email, otp);
    return otp;
  }

  private void validatePasswordChange(ChangePasswordRequest changePasswordRequest, User user) {
    if (changePasswordRequest.getOldPassword().equals(changePasswordRequest.getNewPassword())) {
      throw new CustomException("Old password and new password must be different", HttpStatus.CONFLICT);
    }

    if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
      throw new CustomException("Old password is incorrect", HttpStatus.BAD_REQUEST);
    }

    if (user.getOldPassword() != null && passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getOldPassword())) {
      throw new CustomException("Password has already been used before", HttpStatus.BAD_REQUEST);
    }
  }

  private void saveUserToken(UserDetailsImpl userDetails, String jwtToken, String fingerprinting) {
    AccessTokenDto token = AccessTokenDto.builder()
            .userId(userDetails.getUserEntity().getUserId())
            .fingerprinting(fingerprinting)
            .tokenType("BEARER")
            .expired(false)
            .revoked(false)
            .build();
    authPort.saveAccessTokenInRedis(jwtToken, token);
  }

  private void revokeAllUserTokens(UserDetailsImpl userDetails) {
    Set<Map<String, AccessTokenDto>> validUserTokens = authPort.findAllValidTokenByUser(userDetails);
    if (validUserTokens == null || validUserTokens.isEmpty()) {
      return;
    }

    validUserTokens.forEach(tokenMap -> tokenMap.forEach((key, token) -> {
      token.setExpired(true);
      token.setRevoked(true);
    }));

    authPort.saveAllAccessTokenInRedis(userDetails, validUserTokens);
  }

  private void saveUserRefreshToken(String jwtToken, String fingerprinting, UserDetailsImpl userDetails) {
    authPort.saveRefreshTokenInRedis(jwtToken, fingerprinting, userDetails);
  }
}