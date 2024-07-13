package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.LogoutService;
import com.GHTK.Social_Network.infrastructure.payload.requests.AuthRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ChangePasswordRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.RegisterRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import com.GHTK.Social_Network.application.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  private final LogoutService logoutService;

  @PostMapping("/authentication")
  public ResponseEntity<Object> logIn(@RequestBody @Valid AuthRequest authRequest) {
    try {
      return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.authenticate(authRequest));
    } catch (Exception e) {
      return ResponseHandler.generateErrorResponse(e, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/register")
  public ResponseEntity<Object> signUp(@RequestBody @Valid RegisterRequest registerRequest) {
    try {
      return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.register(registerRequest));
    } catch (Exception e) {
      return ResponseHandler.generateErrorResponse(e, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/change-password")
  public ResponseEntity<Object> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
      try {
        return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authService.changePassword(changePasswordRequest).getMessage());
      } catch (Exception e) {
        return ResponseHandler.generateErrorResponse(e, HttpStatus.BAD_REQUEST);
      }
  }

  @GetMapping("/logout")
  public void logOut(HttpServletRequest request, HttpServletResponse response) {
    logoutService.logout(request, response, null);
  }

}