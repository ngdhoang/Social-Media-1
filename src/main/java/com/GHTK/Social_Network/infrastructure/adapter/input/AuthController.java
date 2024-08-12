package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.AuthPortInput;
import com.GHTK.Social_Network.application.port.input.ProfilePortInput;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.*;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthPortInput authService;
  private final LogoutHandler logoutService;
  private final ProfilePortInput profilePort;

  private Pair<String, String> extractDeviceInfo(HttpServletRequest httpServletRequest) {
    String userAgent = httpServletRequest.getHeader("User-Agent");
    String fingerprinting = httpServletRequest.getHeader("fingerprinting");
    userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0";
    fingerprinting = "231231243124";
    return Pair.of(userAgent, fingerprinting);
  }

  @PostMapping("/authentication")
  public ResponseEntity<Object> logIn(HttpServletRequest request, @RequestBody @Valid AuthRequest authRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.authenticate(authRequest, extractDeviceInfo(request).getLeft(), extractDeviceInfo(request).getRight()));
  }

  @PostMapping("/register")
  public ResponseEntity<Object> signUp(HttpServletRequest request, @RequestBody @Valid RegisterRequest registerRequest) throws MessagingException, UnsupportedEncodingException {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.register(registerRequest, extractDeviceInfo(request).getLeft(), extractDeviceInfo(request).getRight()));
  }

  @PostMapping("/change-password")
  public ResponseEntity<Object> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.changePassword(changePasswordRequest));
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<Object> forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) throws MessagingException, UnsupportedEncodingException {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.forgotPassword(forgotPasswordRequest));
  }

  @PostMapping("/register/check-otp")
  public ResponseEntity<Object> checkOtpRegister(HttpServletRequest request, @RequestBody @Valid RegisterRequest registerRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.checkOtpRegister(registerRequest, extractDeviceInfo(request).getLeft(), extractDeviceInfo(request).getRight(), AuthPortInput.MAX_COUNT_OTP, 100000L));
  }

  @PostMapping("/forgot-password/check-otp")
  public ResponseEntity<Object> checkOtpForgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.checkOtpForgotPassword(forgotPasswordRequest, AuthPortInput.MAX_COUNT_OTP, 100000L));
  }

  @PostMapping("/delete-account")
  public ResponseEntity<Object> deleteAccount() throws MessagingException, UnsupportedEncodingException {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.deleteAccount());
  }

  @DeleteMapping("/delete-account/check-otp")
  public ResponseEntity<Object> checkOtpDeleteAccount(@RequestBody @Valid OTPRequest otpRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.checkOtpDeleteAccount(otpRequest, AuthPortInput.MAX_COUNT_OTP, 100000L));
  }


  @GetMapping("/check-success")
  public ResponseEntity<Object> checkSuccessInNewDevice(HttpServletRequest request, @RequestParam String key) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.checkSuccessDevice(key, extractDeviceInfo(request).getLeft(), extractDeviceInfo(request).getRight()));
  }

  @GetMapping("/verify-token")
  public ResponseEntity<Object> verifyToken() {
    UserDto userDto = profilePort.getProfile(-26022004L);
    if (userDto == null) {
      return ResponseHandler.generateErrorResponse("Profile not found or private", HttpStatus.NOT_FOUND);
    }
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, userDto);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<Object> refreshToken(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
    } else {
      throw new CustomException("Invalid Authorization header", HttpStatus.BAD_REQUEST);
    }
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.refreshToken(token, extractDeviceInfo(request).getRight()));
  }

  @GetMapping("/logout")
  public ResponseEntity<Object> logOut(HttpServletRequest request, HttpServletResponse response) {
    logoutService.logout(request, response, null);
    return ResponseHandler.generateResponse(
            ResponseHandler.MESSAGE_SUCCESS,
            HttpStatus.OK,
            new MessageResponse("Logged out successfully"));
  }

}