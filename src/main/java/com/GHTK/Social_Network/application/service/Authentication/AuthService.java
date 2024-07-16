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
import com.GHTK.Social_Network.infrastructure.payload.requests.AuthRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ChangePasswordRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.RegisterRequest;
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

  private final RedisTemplate<String, AuthRedisDto> authenticationRedisTemplate;

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
  public AuthResponse checkOtp(RegisterRequest registerRequest, int attemptCount, Long timeInterval) {
    if (!authenticationRedisTemplate.opsForValue().get(registerRequest.getUserEmail()).getOtp().equals(registerRequest.getOtp())) {
      AuthRedisDto authRedisDto = authenticationRedisTemplate.opsForValue().get(registerRequest.getUserEmail());
      authRedisDto.setCount(authRedisDto.getCount() + 1);
      authenticationRedisTemplate.opsForValue().set(registerRequest.getUserEmail(), authRedisDto);
      throw new CustomException("Invalid OTP", HttpStatus.BAD_REQUEST);
    }
    if (System.currentTimeMillis() > authenticationRedisTemplate.opsForValue().get(registerRequest.getUserEmail()).getCreateTime().getTime() + timeInterval) {
      authenticationRedisTemplate.delete(registerRequest.getUserEmail());
      throw new CustomException("OTP has expired", HttpStatus.BAD_REQUEST);

    }
    if (authenticationRedisTemplate.opsForValue().get(registerRequest.getUserEmail()).getCount() > attemptCount) {
      authenticationRedisTemplate.delete(registerRequest.getUserEmail());
      throw new CustomException("Maximum OTP attempts exceeded", HttpStatus.TOO_MANY_REQUESTS);
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
    authenticationRedisTemplate.delete(users.getUserEmail());
    return new AuthResponse(
            jwtToken,
            refreshToken,
            users.getRole().toString()
    );
  }

  @Override
  public MessageResponse register(RegisterRequest registerRequest) throws MessagingException, UnsupportedEncodingException {
    if (authenticationRepositoryPort.existsUserByUserEmail(registerRequest.getUserEmail())) {
      throw new CustomException("This Gmail already exists", HttpStatus.CONFLICT);
    }
    String otp = otpPortInput.generateOTP();
    AuthRedisDto authRedisDto = new AuthRedisDto(
            registerRequest,
            otp,
            new Date(),
            5
    );
    authenticationRedisTemplate.opsForValue().set(registerRequest.getUserEmail(), authRedisDto);

    otpPortInput.sendOtpEmail(registerRequest.getUserEmail(), otp);


    return new MessageResponse("Otp check in email");
  }

  @Override
  public MessageResponse changePassword(ChangePasswordRequest changePasswordRequest) {
    User user = getUserAuth();

    if (changePasswordRequest.getOldPassword().equals(changePasswordRequest.getNewPassword()))
      throw new CustomException("Old password and new password must be different", HttpStatus.CONFLICT);

    if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
      throw new CustomException("Old password is incorrect", HttpStatus.BAD_REQUEST);

    if (user.getOldPassword() != null && passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getOldPassword()))
      throw new CustomException("Password has already been used before", HttpStatus.BAD_REQUEST);

    String encodeNewPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
    authenticationRepositoryPort.changePassword(encodeNewPassword, user.getUserId());

    UserDetailsImpl userDetails = new UserDetailsImpl(user);
    revokeAllUserTokens(userDetails);

    return new MessageResponse("Password changed successfully");
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
