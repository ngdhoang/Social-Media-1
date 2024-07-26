package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.post.CommentPostInput;
import com.GHTK.Social_Network.application.port.input.post.ImagePostInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.CommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommentController {
  private final CommentPostInput commentPostInput;
  private final ImagePostInput imagePostInput;

  @PostMapping("/comments")
  public ResponseEntity<Object> createComment(@RequestBody @Valid CommentRequest commentRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.createCommentSrc(commentRequest));
  }

  @PostMapping("/comments/{parentCommentId}/replies")
  public ResponseEntity<Object> createReply(@PathVariable Long parentCommentId, @RequestBody @Valid CommentRequest commentRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.createCommentChild(parentCommentId, commentRequest));
  }

  @GetMapping("/{postId}/comments")
  public ResponseEntity<Object> getAllCommentForPost(@PathVariable Long postId) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.getCommentsByPostId(postId));
  }

  @GetMapping("/comments/{commentId}")
  public ResponseEntity<Object> getCommentById(@PathVariable Long commentId) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.getCommentById(commentId));
  }

  @GetMapping("/comments")
  public ResponseEntity<Object> getCommentsByInteractions() {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.getCommentsByInteractions());
  }


  @GetMapping("/comments/{commentId}/replies")
  public ResponseEntity<Object> getAllCommentChildByCommentParentId(@PathVariable Long commentId) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.getAllCommentChildById(commentId));
  }

  @DeleteMapping("/comments/{commentId}")
  public ResponseEntity<Object> deleteComment(@PathVariable Long commentId) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.deleteComment(commentId));
  }

  @PutMapping("/comments/{commentId}")
  public ResponseEntity<Object> updateComment(@PathVariable Long commentId, @RequestBody @Valid CommentRequest commentRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.updateComment(commentId, commentRequest));
  }

  @PostMapping("/comments/images")
  public ResponseEntity<Object> addImageToComment(@RequestParam MultipartFile image) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, imagePostInput.createImage(image, ImagePostInput.COMMENT_TAIL));
  }
}

