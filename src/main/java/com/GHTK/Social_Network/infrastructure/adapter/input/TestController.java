package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.DevicePortInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.AuthRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.VerifyDeviceRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {
    private final DevicePortInput devicePortInput;

    @PostMapping("")
    public ResponseEntity<Object> checkNewDevice(){
        String email = "nguyen@gmail.com";
        return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK,devicePortInput.checkDevice(email,1L,"a","b","b"));
    }

    @PostMapping("/verify")
    public ResponseEntity<Object> validateOtp(@PathVariable @Valid String otp){
        VerifyDeviceRequest verifyDeviceRequest = new VerifyDeviceRequest("077499_1","b","b","b");
        return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK,devicePortInput.checkOtpLoginNewDevice(otp,verifyDeviceRequest));
    }
}
