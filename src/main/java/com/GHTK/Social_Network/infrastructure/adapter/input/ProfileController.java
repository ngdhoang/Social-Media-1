package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.ProfilePortInput;
import com.GHTK.Social_Network.infrastructure.payload.dto.UserDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.ProfileStateRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateProfileRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"/api/profile"})
@RequiredArgsConstructor
public class ProfileController {
  private final ProfilePortInput profilePort;

  @GetMapping("")
  public ResponseEntity<Object> getProfile(@RequestParam("i") Long id) {
    UserDto userDto = profilePort.getProfile(id);
    if (userDto == null) {
      return ResponseHandler.generateErrorResponse("Profile not found or private", HttpStatus.NOT_FOUND);
    }
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, userDto);
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
  public ResponseEntity<Object> updateAvatarProfile(@RequestParam(value = "file") @Valid @NotNull(message = "File cannot null") MultipartFile avatar) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, profilePort.updateAvatarProfile(avatar));
  }
}
