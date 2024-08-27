package com.GHTK.Social_Network.infrastructure.adapter.input.chatController;

import com.GHTK.Social_Network.application.port.input.chat.CallVideoPortInput;
import com.GHTK.Social_Network.infrastructure.payload.dto.chat.MessageDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chat")
public class CallVideoController {
  private final CallVideoPortInput callVideoPortInput;

  @PostMapping("/video-call/{groupId}")
  public ResponseEntity<Object> ring(@PathVariable String groupId) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, callVideoPortInput.ring(groupId));
  }

  @PostMapping("/reject-video-call/{groupId}")
  public ResponseEntity<Object> reject(@PathVariable String groupId) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, callVideoPortInput.reject(groupId));
  }

//
//  @MessageMapping("/exchange-signal")
//  public void addUserToChanel(@Payload MessageDto message) {
//    callVideoPortInput.exchangeSignal(message);
//  }
}
