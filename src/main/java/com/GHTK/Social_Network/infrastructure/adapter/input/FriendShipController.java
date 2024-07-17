package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.FriendShipPortInput;
import com.GHTK.Social_Network.application.service.FriendShipService;
import com.GHTK.Social_Network.infrastructure.payload.requests.AcceptFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.SetRequestFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UnFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friendship")
@RequiredArgsConstructor
public class FriendShipController {

  private final FriendShipPortInput friendShipService;

  @GetMapping("")
  public ResponseEntity<Object> getFriendShip(
          @ModelAttribute GetFriendShipRequest getFriendShipRequest
  ) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, friendShipService.getFriendShip(getFriendShipRequest));
  }

  @PostMapping("/set-request")
  public ResponseEntity<Object> setRequestFriendShip(@RequestBody @Valid SetRequestFriendRequest setRequestFriendRequest) {
    System.out.println("hello");

    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, friendShipService.setRequestFriendShip(setRequestFriendRequest));
  }

  @PostMapping("/accept-request")
  public ResponseEntity<Object> acceptRequestFriendShip(@RequestBody @Valid AcceptFriendRequest acceptFriendRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, friendShipService.acceptRequestFriendShip(acceptFriendRequest));

  }

  @PostMapping("/un-friendship")
  public ResponseEntity<Object> unFriendShip(@RequestBody @Valid UnFriendShipRequest unFriendShipRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, friendShipService.unFriendShip(unFriendShipRequest));

  }
}
