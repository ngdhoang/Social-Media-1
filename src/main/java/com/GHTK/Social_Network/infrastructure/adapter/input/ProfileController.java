package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.ProfilePortInput;
import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.ProfileStateRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateProfileRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/profile"})
@RequiredArgsConstructor
public class ProfileController {
  private final ProfilePortInput profilePort;
  
  @GetMapping("")
  public ResponseEntity<Object> getProfile(@RequestParam("i") Long id) {
    ProfileDto profileDto = profilePort.getProfile(id);
    if (profileDto == null) {
      return ResponseHandler.generateErrorResponse("Profile not found or private", HttpStatus.NOT_FOUND);
    }
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, profileDto);
  }

  @PutMapping("")
  public ResponseEntity<Object> updateProfile(@RequestBody @Valid UpdateProfileRequest updateProfileRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, profilePort.updateProfile(updateProfileRequest));
  }

  @PutMapping("/state")
  public ResponseEntity<Object> updateProfile(@RequestBody @Valid ProfileStateRequest profileStateRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, profilePort.setStateProfile(profileStateRequest));
  }

  @PutMapping("/change-avatar")
  public ResponseEntity<Object> updateAvatarProfile(@RequestBody @Valid ImageDto imageDto) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, profilePort.updateAvatarProfile(imageDto));
  }
}
