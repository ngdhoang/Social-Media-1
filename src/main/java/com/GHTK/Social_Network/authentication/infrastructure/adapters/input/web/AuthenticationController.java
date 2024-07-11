package com.GHTK.Social_Network.authentication.infrastructure.adapters.input.web;

import com.GHTK.Social_Network.authentication.application.payloads.requests.LoginRequest;
import com.GHTK.Social_Network.authentication.application.payloads.requests.RegisterRequest;
import com.GHTK.Social_Network.authentication.application.payloads.responses.ResponseHandler;
import com.GHTK.Social_Network.authentication.application.services.AuthenticationService;
import com.GHTK.Social_Network.authentication.infrastructure.adapters.output.security.sevices.LogoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  private final LogoutService logoutService;

  @PostMapping("/log-in")
  public ResponseEntity<Object> logIn(@RequestBody @Valid LoginRequest loginRequest) {
    try {
      return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authenticationService.logIn(loginRequest));
    } catch (Exception e) {
      return ResponseHandler.generateErrorResponse(e);
    }
  }

  @PostMapping("/sign-up")
  public ResponseEntity<Object> signUp(@RequestBody @Valid RegisterRequest registerRequest) {
    try {
      return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authenticationService.signUp(registerRequest));
    } catch (Exception e) {
      return ResponseHandler.generateErrorResponse(e);
    }
  }
}