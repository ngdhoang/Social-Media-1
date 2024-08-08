package com.GHTK.Social_Network.application.service.auth;

import com.GHTK.Social_Network.application.port.input.AuthPortInput;
import com.GHTK.Social_Network.application.port.input.post.DevicePortInput;
import com.GHTK.Social_Network.application.port.output.OtpPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.auth.DevicePort;
import com.GHTK.Social_Network.application.port.output.auth.JwtPort;
import com.GHTK.Social_Network.application.port.output.auth.RedisAuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.user.Device;
import com.GHTK.Social_Network.domain.model.user.EDeviceType;
import com.GHTK.Social_Network.domain.model.user.ERole;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.UserNode;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.node.UserNodeRepository;
import com.GHTK.Social_Network.infrastructure.payload.dto.AccessTokenDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.AuthRedisDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.*;
import com.GHTK.Social_Network.infrastructure.payload.responses.AuthResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.OTPDeviceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

  private final UserNodeRepository userNodeRepository;
  private final JwtPort jwtUtils;
  private final RedisAuthPort redisAuthPort;
  private final DevicePort devicePort;
  private final DevicePortInput devicePortInput;

  @Override
  public Object authenticate(AuthRequest authRequest, String userAgent, String fingerprinting) {
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
    if (devicePort.getDevice(fingerprinting, userAgent, user.getUserId()) == null) {
      Map<String, Object> otpData = devicePortInput.generateOtp(3);
      Integer chosenNumber = (Integer) otpData.get("selectedNumber");
      List<Integer> listNumber = (List<Integer>) otpData.get("generatedNumbers");

      List<String> otpList = listNumber.stream()
              .map(Object::toString)
              .collect(Collectors.toList());

      String key = generateKeyDevicePing();
      redisAuthPort.createOrUpdate(chosenNumber + RedisAuthPort.DEVICE_TAIL + user.getUserEmail(),
              AuthRedisDto.builder()
                      .fingerprinting(fingerprinting)
                      .userAgent(userAgent)
                      .otp(otpList)
                      .key(key)
                      .build());

      return new OTPDeviceResponse(chosenNumber, key);
    }

    return authenticationWithDeviceTrust(user, fingerprinting);
  }

  private AuthResponse authenticationWithDeviceTrust(User user, String fingerprinting) {
    UserDetailsImpl userDetails = authPort.getUserDetails(user);
    var jwtToken = jwtUtils.generateToken(userDetails, fingerprinting);
    var refreshToken = jwtUtils.generateRefreshToken(userDetails, fingerprinting);
    revokeAllUserTokens(userDetails);
    saveUserToken(userDetails, jwtToken, fingerprinting);

    return new AuthResponse(jwtToken, refreshToken, user.getRole().toString());
  }

  @Override
  public Object checkSuccessDevice(String key, String userAgent, String fingerprinting) {
    String newKey =  key + "_" +fingerprinting + "_" + userAgent + RedisAuthPort.DEVICE_CHECK_TAIL;
    if (redisAuthPort.existsByKey(newKey)) {
      AuthRedisDto authRedisDto = redisAuthPort.findByKey(newKey);
      String email = authRedisDto.getRegisterRequest().getUserEmail();
      var user = authPort.findByEmail(email)
              .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
      redisAuthPort.deleteByKey(newKey);
      return authenticationWithDeviceTrust(user, fingerprinting);
    }
    return new MessageResponse("Nothing...");
  }

  @Override
  public MessageResponse checkOtpRegister(RegisterRequest registerRequest, String userAgent, String fingerprinting, int attemptCount, Long timeInterval) {
    validateOtp(registerRequest.getUserEmail(), registerRequest.getOtp(), attemptCount, timeInterval);

    User userSave = createUser(registerRequest);
    userSave = authPort.saveUser(userSave);
    UserDetailsImpl userDetails = authPort.getUserDetails(userSave);
    String jwtToken = jwtUtils.generateToken(userDetails, fingerprinting);
    saveUserToken(userDetails, jwtToken, fingerprinting);
    System.out.println(userSave);

    // save new device default
    Device newDevice = new Device
            (
                    fingerprinting,
                    userAgent,
                    EDeviceType.DEFAULT,
                    userSave.getUserId());
    devicePort.saveDevice(
            newDevice,
            userSave.getUserId()
    );

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
  public MessageResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
    if (!authPort.existsUserByUserEmail(forgotPasswordRequest.getUserEmail())) {
      throw new CustomException("This email doesn't exist", HttpStatus.NOT_FOUND);
    }

    String otp = otpPort.generateOtp();
    saveOtpToRedis(forgotPasswordRequest.getUserEmail(), otp);
    otpPort.sendOtpEmail(forgotPasswordRequest.getUserEmail(), otp);

    return new MessageResponse("OTP sent to email");
  }

  @Override
  public MessageResponse deleteAccount() {
    String otp = otpPort.generateOtp();
    saveOtpToRedis(authPort.getUserAuth().getUserEmail(), otp);
    otpPort.sendOtpEmail(authPort.getUserAuth().getUserEmail(), otp);

    return new MessageResponse("OTP sent to email");
  }

  @Override
  public AuthResponse refreshToken(String refreshToken, String fingerprinting) {
    Pair<UserDetailsImpl, String> infoAuth = authPort.refreshToken(refreshToken, fingerprinting);
    if (infoAuth == null) {
      throw new CustomException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
    }
    revokeAllUserTokens(infoAuth.getLeft());
    saveUserToken(infoAuth.getLeft(), infoAuth.getRight(), fingerprinting);
    var user = authPort.findByEmail(infoAuth.getLeft().getUsername())
            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

    return new AuthResponse(infoAuth.getRight(), "", user.getRole().toString());
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

  private void saveUserToken(UserDetailsImpl userDetails, String jwtToken, String fingerprinting) {
    AccessTokenDto token = AccessTokenDto.builder()
            .userId(userDetails.getUserEntity().getUserId())
            .fingerprinting(fingerprinting)
            .tokenType("BEARER")
            .expired(false)
            .revoked(false)
            .build();
    System.out.println(token);
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

  private String generateKeyDevicePing(){
    UUID uuid = UUID.randomUUID();
    return uuid.toString();
  }

}