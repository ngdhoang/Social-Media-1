package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.ProfilePortInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.profile.UpdateProfilePrivacyRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.profile.UpdateProfileRequest;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.ProfileStateDto;

import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {
  private final ProfilePortInput profilePort;

  @GetMapping("/{id}")
  public ResponseEntity<Object> getProfile(@PathVariable Long id) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, profilePort.getProfile(id));
  }

  @PutMapping("")
  public ResponseEntity<Object> updateProfile(@RequestBody @Valid UpdateProfileRequest updateProfileRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, profilePort.updateProfile(updateProfileRequest));
  }

  @PatchMapping("/state")
  public ResponseEntity<Object> updateProfileState(@RequestBody @Valid ProfileStateDto profileStateRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, profilePort.setStateProfile(profileStateRequest));
  }

  @PatchMapping("/privacy")
  public ResponseEntity<Object> setProfilePrivacy(@RequestBody @Valid UpdateProfilePrivacyRequest profilePrivacyRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, profilePort.setProfilePrivacy(profilePrivacyRequest));
  }

  @PatchMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Object> updateAvatarProfile(
          @RequestParam("avatar") @Valid @NotNull(message = "Avatar file cannot be null") MultipartFile avatar) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, profilePort.updateAvatarProfile(avatar));
  }

  @PatchMapping(value = "/background", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Object> updateBackgroundProfile(
          @RequestParam("background") @Valid @NotNull(message = "Background file cannot be null") MultipartFile background) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, profilePort.updateBackgroundProfile(background));
  }
}

