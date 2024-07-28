package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.post.ReactionPostInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ReactionPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ReactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/reactions/posts")
@RequiredArgsConstructor
public class ReactionPostController {
  private final ReactionPostInput reactionPostInput;

  @PostMapping("/{p}")
  public ResponseEntity<Object> reactionPostHandler(@PathVariable Long p, @RequestBody @Valid ReactionPostRequest reactionPostRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, reactionPostInput.handleReactionPost(p, reactionPostRequest));
  }

  @GetMapping("/{p}")
  public ResponseEntity<Object> getReactionPostHandler(@PathVariable Long p, @Valid @ModelAttribute GetReactionPostRequest getReactionPostRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, reactionPostInput.getListReactionInPost(p, getReactionPostRequest));
  }
}
