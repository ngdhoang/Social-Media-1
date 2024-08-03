package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.post.ReactionCommentInput;
import com.GHTK.Social_Network.application.port.input.post.ReactionPostInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionCommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ReactionRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/reaction")
@RequiredArgsConstructor
public class ReactionPostController {
  private final ReactionPostInput reactionPostInput;

  private final ReactionCommentInput reactionCommentInput;

  @PostMapping("/post/{p}")
  public ResponseEntity<Object> reactionPostHandler(@PathVariable Long p, @RequestBody @Valid ReactionRequest reactionPostRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, reactionPostInput.handleReactionPost(p, reactionPostRequest));
  }

  @GetMapping("/post/{p}")
  public ResponseEntity<Object> getReactionPostHandler(@PathVariable Long p, @Valid @ModelAttribute GetReactionPostRequest getReactionPostRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, reactionPostInput.getListReactionInPost(p, getReactionPostRequest));
  }

  @PostMapping("/comment/{p}")
  public ResponseEntity<Object> reactionCommentHandler(@PathVariable Long p, @RequestBody @Valid ReactionRequest reactionPostRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, reactionCommentInput.handleReactionComment(p, reactionPostRequest));
  }

  @GetMapping("/comment/{p}")
  public ResponseEntity<Object> getReactionPostHandler(@PathVariable Long p, @Valid @ModelAttribute GetReactionCommentRequest getReactionCommentRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, reactionCommentInput.getListReactionInComment(p, getReactionCommentRequest));
  }

}