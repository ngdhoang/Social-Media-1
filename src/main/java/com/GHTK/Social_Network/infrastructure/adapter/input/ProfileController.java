package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.service.ProfileService;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.payload.requests.ProfileRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    // truyen idUser(nguoi dung muon xem) , idProfile id cua User chinh chu Profile
    @GetMapping("/view")
    public ResponseEntity<Object> takeProfile(@RequestBody ProfileRequest profileRequest) {
        try {
            return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, profileService.takeProfile(profileRequest));
        }catch (Exception e) {
            return ResponseHandler.generateErrorResponse(e, HttpStatus.BAD_REQUEST);
        }
    }
    // idViewUser: User view Profile
    @PostMapping("/edit")
    public ResponseEntity<Object> editProfile(@RequestBody ProfileRequest profileRequest) {
        try {
            return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK,profileService.updateProfile(profileRequest));
        }catch (Exception e) {
            return ResponseHandler.generateErrorResponse(e, HttpStatus.BAD_REQUEST);
        }
    }
}
