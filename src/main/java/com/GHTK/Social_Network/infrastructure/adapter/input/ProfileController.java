package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.ProfilePortInput;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/profile"})
@RequiredArgsConstructor
public class ProfileController {
  private final ProfilePortInput profilePort;

  @GetMapping("/view")
  public ResponseEntity<Object> getProfile(@RequestParam("i") Long id) {
    try {
      ProfileDto profileDto = profilePort.getProfile(id);
      if (profileDto == null) {
        return ResponseHandler.generateErrorResponse("Profile not found or private", HttpStatus.NOT_FOUND);
      }
      return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, profileDto);
    } catch (Exception e) {
      return ResponseHandler.generateErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/update")
  public ResponseEntity<Object> updateProfile(@RequestBody ProfileDto profileDto) {
    try {
      return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, profilePort.updateProfile(profileDto));
    } catch (Exception e) {
      return ResponseHandler.generateErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/update/state")
  public ResponseEntity<Object> updateProfile(@RequestParam("s") Integer state) {
    try {
      return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, profilePort.setStateProfile(state));
    } catch (Exception e) {
      return ResponseHandler.generateErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
