package com.GHTK.Social_Network.infrastructure.adapter.input.chatController;

import com.GHTK.Social_Network.application.port.input.chat.MessagePortInput;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chat")
public class MessageController {
  private final MessagePortInput messagePortInput;

  @PutMapping("/delete/{msgId}")
  public ResponseEntity<Object> deleteMessage(@PathVariable String msgId) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, messagePortInput.deleteMassage(msgId));
  }
}
