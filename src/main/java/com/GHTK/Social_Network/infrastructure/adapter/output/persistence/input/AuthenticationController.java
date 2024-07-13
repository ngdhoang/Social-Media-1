package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.input;

import com.GHTK.Social_Network.infrastructure.adapter.output.persistence.input.security.service.LogoutService;
import com.GHTK.Social_Network.infrastructure.payload.requests.AuthenticationRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.RegisterRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import com.GHTK.Social_Network.application.service.AuthenticationService;
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

  @PostMapping("/authentication")
  public ResponseEntity<Object> logIn(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
    try {
      return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authenticationService.authenticate(authenticationRequest));
    } catch (Exception e) {
      return ResponseHandler.generateErrorResponse(e, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/register")
  public ResponseEntity<Object> signUp(@RequestBody @Valid RegisterRequest registerRequest) {
    try {
      return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, authenticationService.register(registerRequest));
    } catch (Exception e) {
      return ResponseHandler.generateErrorResponse(e, HttpStatus.BAD_REQUEST);
    }
  }
}