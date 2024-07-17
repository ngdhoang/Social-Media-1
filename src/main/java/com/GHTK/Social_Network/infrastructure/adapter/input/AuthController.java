package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.service.Authentication.AuthService;
import com.GHTK.Social_Network.infrastructure.payload.requests.AuthRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ChangePasswordRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.RegisterRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  private final LogoutHandler logoutService;

  @PostMapping("/authentication")
  public ResponseEntity<Object> logIn(@RequestBody @Valid AuthRequest authRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.authenticate(authRequest));
  }

  @PostMapping("/register")
  public ResponseEntity<Object> signUp(@RequestBody @Valid RegisterRequest registerRequest) throws MessagingException, UnsupportedEncodingException {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.register(registerRequest));
  }

  @PostMapping("/change-password")
  public ResponseEntity<Object> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.changePassword(changePasswordRequest));
  }

//  @PostMapping("/forgot-password")
//  public ResponseEntity<Object> forgotPassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
//    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.forgotPassword(changePasswordRequest));
//  }

  @PostMapping("/check-otp")
  public ResponseEntity<Object> checkOtp(@RequestBody @Valid RegisterRequest registerRequest) {
    System.out.println(registerRequest.getOtp());
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.checkOtp(registerRequest, 3, 10000L));
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