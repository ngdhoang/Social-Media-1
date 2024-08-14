package com.GHTK.Social_Network.infrastructure.adapter.input.chatController;

import com.GHTK.Social_Network.application.port.input.chat.MessagePortInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ReactionRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chat")
public class MessageController {
  private final MessagePortInput messagePortInput;

  @DeleteMapping("/delete/{msgId}")
  public ResponseEntity<Object> deleteMessage(@PathVariable String msgId) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, messagePortInput.deleteMessage(msgId));
  }

  @DeleteMapping("/recall/{msgId}")
  public ResponseEntity<Object> recallMessage(@PathVariable String msgId) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, messagePortInput.recallMessage(msgId));
  }

  @PostMapping(value = "/send/image/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Object> sendListMessage(
          @PathVariable String groupId,
          @RequestParam("image") @Valid @NotNull(message = "Image cannot be null") List<MultipartFile> images) {
    if (images.size() > 50) {
      return ResponseHandler.generateResponse("You can only upload up to 50 images", HttpStatus.BAD_REQUEST, null);
    }
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, messagePortInput.sendListImage(images, groupId));
  }

  @PostMapping("/reaction/{msgId}")
  public ResponseEntity<Object> reactionMessage(@PathVariable String msgId, @RequestBody ReactionRequest reactionRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, messagePortInput.reactionMessage(msgId, reactionRequest));
  }

  @GetMapping("/reaction/{msgId}")
  public ResponseEntity<Object> getReactionMessage(@PathVariable String msgId, @RequestParam String status) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, messagePortInput.getReactionMessage(msgId, status));
  }

  @GetMapping("/messages/{groupId}")
  public ResponseEntity<Object> getMessage(@PathVariable String groupId, @ModelAttribute @Valid PaginationRequest paginationRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, messagePortInput.getMessages(groupId, paginationRequest));
  }

  @PutMapping("/messages/read/{msgId}")
  public ResponseEntity<Object> readMessage(@PathVariable String msgId) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, messagePortInput.readMessages(msgId));
  }
}
