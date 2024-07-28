package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.BlockPortInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetBlockRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.SetBlockRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.UnFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/block")
@RequiredArgsConstructor
public class BlockController {
    private final BlockPortInput blockService;

    @GetMapping("")
    public ResponseEntity<Object> getListBlock(
            @Valid @ModelAttribute GetBlockRequest getBlockRequest
    ) {
        return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, blockService.getListBlock(getBlockRequest));
    }

    @PostMapping("")
    public ResponseEntity<Object> blockRequest(@RequestBody @Valid SetBlockRequest setBlockRequest) {
        return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, blockService.blockRequest(setBlockRequest));
    }


    @DeleteMapping("")
    public ResponseEntity<Object> unBlockRequest(@RequestBody @Valid UnFriendShipRequest unFriendShipRequest) {
        return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, blockService.unBlockRequest(unFriendShipRequest));
    }

}