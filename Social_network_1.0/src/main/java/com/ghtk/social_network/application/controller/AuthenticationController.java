package com.ghtk.social_network.application.controller;

import com.ghtk.social_network.application.request.LoginRequest;
import com.ghtk.social_network.application.request.RegisterRequest;
import com.ghtk.social_network.application.responce.ResponseHandler;
import com.ghtk.social_network.domain.service.AuthenticationService;
import com.ghtk.social_network.domain.service.securityservice.LogoutService;
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