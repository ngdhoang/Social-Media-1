package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.post.CommentPostInput;
import com.GHTK.Social_Network.application.port.input.post.ImagePostInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.CommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.CreateImageRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class CommentController {
  private final CommentPostInput commentPostInput;

  private final ImagePostInput imagePostInput;

  @PostMapping("/comment")
  public ResponseEntity<Object> postComment(@RequestBody @Valid CommentRequest commentRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.createCommentSrc(commentRequest));
  }

  @PostMapping("/comment/{u}")
  public ResponseEntity<Object> postCommentChild(@PathVariable Long u, @RequestBody @Valid CommentRequest commentRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.createCommentChild(u, commentRequest));
  }

  @GetMapping("{id}/comment")
  public ResponseEntity<Object> getCommentPost(@PathVariable Long id) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.getCommentsByPostId(id));
  }

  @DeleteMapping("/comment/{id}/delete")
  public ResponseEntity<Object> deleteComment(@PathVariable Long id) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.deleteComment(id));
  }

  @PutMapping("/comment/{u}/update")
  public ResponseEntity<Object> updateComment(@PathVariable Long u, @RequestBody @Valid CommentRequest commentRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.updateComment(u, commentRequest));
  }

  @PutMapping("/comment/up-image")
  public ResponseEntity<Object> upImageComment(@ModelAttribute @Valid CreateImageRequest request) throws IOException {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, imagePostInput.createImage(request));
  }
}
