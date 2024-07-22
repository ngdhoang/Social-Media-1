package com.GHTK.Social_Network.application.service.Authentication;

import com.GHTK.Social_Network.application.port.input.AuthPortInput;
import com.GHTK.Social_Network.application.port.input.OtpPortInput;
import com.GHTK.Social_Network.application.port.output.AuthPort;
import com.GHTK.Social_Network.domain.entity.user.ERole;
import com.GHTK.Social_Network.domain.entity.user.Token;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.jwt.JwtUtils;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import com.GHTK.Social_Network.infrastructure.exception.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.dto.AuthRedisDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.*;
import com.GHTK.Social_Network.infrastructure.payload.responses.AuthResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements AuthPortInput {
  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;
  private final PasswordEncoder passwordEncoder;
  private final AuthPort authenticationRepositoryPort;
  private final OtpPortInput otpPortInput;
  private final RedisTemplate<String, AuthRedisDto> redisTemplate;

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

    return authenticationRepositoryPort.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid token"));
  }

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

    var user = authenticationRepositoryPort.findByEmail(authRequest.getUserEmail())
            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
    UserDetailsImpl userDetails = new UserDetailsImpl(user);
    var jwtToken = jwtUtils.generateToken(userDetails);
    var refreshToken = jwtUtils.generateRefreshToken(userDetails);
    revokeAllUserTokens(userDetails);
    saveUserToken(userDetails, jwtToken);

    return new AuthResponse(jwtToken, refreshToken, user.getRole().toString());
  }

  @Override
  public MessageResponse checkOtpRegister(RegisterRequest registerRequest, int attemptCount, Long timeInterval) {
    validateOtp(registerRequest.getUserEmail(), registerRequest.getOtp(), attemptCount, timeInterval);

    User user = createUser(registerRequest);
    UserDetailsImpl userDetails = new UserDetailsImpl(user);
    authenticationRepositoryPort.saveUser(user);
    String jwtToken = jwtUtils.generateToken(userDetails);
    saveUserToken(userDetails, jwtToken);

    return new MessageResponse("Registration successful");
  }

  @Override
  public MessageResponse checkOtpForgotPassword(ForgotPasswordRequest forgotPasswordRequest, int attemptCount, Long timeInterval) {
    validateOtp(forgotPasswordRequest.getUserEmail(), forgotPasswordRequest.getOtp(), attemptCount, timeInterval);

    var user = authenticationRepositoryPort.findByEmail(forgotPasswordRequest.getUserEmail())
            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
    UserDetailsImpl userDetails = new UserDetailsImpl(user);
    revokeAllUserTokens(userDetails);

    if (passwordEncoder.matches(forgotPasswordRequest.getNewPassword(), user.getPassword())) {
      throw new CustomException("Old password and new password must be different", HttpStatus.CONFLICT);
    }

    String encodeNewPassword = passwordEncoder.encode(forgotPasswordRequest.getNewPassword());
    authenticationRepositoryPort.changePassword(encodeNewPassword, user.getUserId());

    return new MessageResponse("Password changed");
  }

  @Override
  public MessageResponse checkOtpDeleteAccount(OTPRequest otpRequest, int attemptCount, Long timeInterval) {
    validateOtp(getUserAuth().getUserEmail(), otpRequest.getOtp(), attemptCount, timeInterval );

    authenticationRepositoryPort.deleteUserByEmail(getUserAuth().getUserEmail());

    return new MessageResponse("Account deleted");
  }

  @Override
  public MessageResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) throws MessagingException, UnsupportedEncodingException {
    if (!authenticationRepositoryPort.existsUserByUserEmail(forgotPasswordRequest.getUserEmail())) {
      throw new CustomException("This email doesn't exist", HttpStatus.NOT_FOUND);
    }

    String otp = otpPortInput.generateOTP();
    saveOtpToRedis(forgotPasswordRequest.getUserEmail(), otp);
    otpPortInput.sendOtpEmail(forgotPasswordRequest.getUserEmail(), otp);

    return new MessageResponse("OTP sent to email");
  }

  @Override
  public MessageResponse deleteAccount() throws MessagingException, UnsupportedEncodingException {
    String otp = otpPortInput.generateOTP();
    saveOtpToRedis(getUserAuth().getUserEmail(), otp);
    otpPortInput.sendOtpEmail(getUserAuth().getUserEmail(), otp);

    return new MessageResponse("OTP sent to email");
  }

  @Override
  public MessageResponse register(RegisterRequest registerRequest) throws MessagingException, UnsupportedEncodingException {
    if (authenticationRepositoryPort.existsUserByUserEmail(registerRequest.getUserEmail())) {
      throw new CustomException("This email already exists", HttpStatus.CONFLICT);
    }

    String otp = otpPortInput.generateOTP();
    saveOtpToRedis(registerRequest.getUserEmail(), otp);
    otpPortInput.sendOtpEmail(registerRequest.getUserEmail(), otp);

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
    authenticationRepositoryPort.changePassword(encodeNewPassword, user.getUserId());

    UserDetailsImpl userDetails = new UserDetailsImpl(user);
    revokeAllUserTokens(userDetails);

    return new MessageResponse("Password changed successfully");
  }

  private void validateOtp(String email, String providedOtp, int maxAttempts, long timeInterval) {
    AuthRedisDto authRedisDto = redisTemplate.opsForValue().get(email);
    if (authRedisDto == null) {
      throw new CustomException("OTP not found", HttpStatus.BAD_REQUEST);
    }

    if (authRedisDto.getCount() >= maxAttempts) {
      redisTemplate.delete(email);
      throw new CustomException("Maximum OTP attempts exceeded", HttpStatus.TOO_MANY_REQUESTS);
    }

    if (System.currentTimeMillis() > authRedisDto.getCreateTime().getTime() + timeInterval) {
      redisTemplate.delete(email);
      throw new CustomException("OTP has expired", HttpStatus.BAD_REQUEST);
    }

    if (!authRedisDto.getOtp().equals(providedOtp)) {
      authRedisDto.setCount(authRedisDto.getCount() + 1);
      redisTemplate.opsForValue().set(email, authRedisDto);
      throw new CustomException("Invalid OTP", HttpStatus.BAD_REQUEST);
    }

    redisTemplate.delete(email);
  }

  private void saveOtpToRedis(String email, String otp) {
    AuthRedisDto authRedisDto = new AuthRedisDto(null, otp, new Date(), 0);
    redisTemplate.opsForValue().set(email, authRedisDto);
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
            .user(userDetails.getUser())
            .token(jwtToken)
            .tokenType("BEARER")
            .expired(false)
            .revoked(false)
            .build();
    authenticationRepositoryPort.saveToken(token);
  }

  private void revokeAllUserTokens(UserDetailsImpl userDetails) {
    var validUserTokens = authenticationRepositoryPort.findAllValidTokenByUser(userDetails.getUser().getUserId());
    if (validUserTokens.isEmpty()) {
      return;
    }
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    authenticationRepositoryPort.saveAll(validUserTokens);
  }
}