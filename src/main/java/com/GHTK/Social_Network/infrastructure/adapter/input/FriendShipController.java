package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.FriendShipPortInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.AcceptFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.GetFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.SetRequestFriendRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.relationship.UnFriendShipRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendShipController {

  private final FriendShipPortInput friendShipService;

  @GetMapping("")
  public ResponseEntity<Object> getListFriend(
          @Valid @ModelAttribute GetFriendShipRequest getFriendShipRequest
  ) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, friendShipService.getListFriend(getFriendShipRequest));
  }

  @PostMapping("")
  public ResponseEntity<Object> createRequestFriend(@RequestBody @Valid SetRequestFriendRequest setRequestFriendRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, friendShipService.createRequestFriend(setRequestFriendRequest));
  }


  @PostMapping("/accept")
  public ResponseEntity<Object> acceptRequestFriendShip(@RequestBody @Valid AcceptFriendRequest acceptFriendRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, friendShipService.acceptFriendRequest(acceptFriendRequest));
  }

  @PutMapping("")
  public ResponseEntity<Object> updateStateFriend(@RequestBody @Valid SetRequestFriendRequest setRequestFriendRequest){
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, friendShipService.updateStateFriend(setRequestFriendRequest));
  }

  @DeleteMapping("")
  public ResponseEntity<Object> unFriendShip(@RequestBody @Valid UnFriendShipRequest unFriendShipRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, friendShipService.unFriendRequest(unFriendShipRequest));
  }

}