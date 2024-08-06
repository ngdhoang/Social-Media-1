package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.post.DevicePortInput;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {
  private final DevicePortInput devicePortInput;

  @PostMapping("/verify-otp")
  public ResponseEntity<Object> verifyOtpInOldDevice(@RequestParam String otp) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, devicePortInput.checkDeviceOtp(Integer.parseInt(otp)));
  }
}
