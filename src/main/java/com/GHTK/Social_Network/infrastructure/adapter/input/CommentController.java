package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.post.CommentPostInput;
import com.GHTK.Social_Network.application.port.input.post.ImagePostInput;
//import com.GHTK.Social_Network.application.port.input.post.ReactionCommentInput;
//import com.GHTK.Social_Network.infrastructure.payload.requests.ReactionRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetCommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.CommentRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class CommentController {
  private final CommentPostInput commentPostInput;
  private final ImagePostInput imagePostInput;
//  private final ReactionCommentInput reactionCommentInput;

  @PostMapping("/comments")
  public ResponseEntity<Object> createRootComment(@RequestBody @Valid CommentRequest commentRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.createCommentRoot(commentRequest));
  }

  @PostMapping("/comments/{parentCommentId}")
  public ResponseEntity<Object> createChildComment(@PathVariable Long parentCommentId, @RequestBody @Valid CommentRequest commentRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.createCommentChild(parentCommentId, commentRequest));
  }

  @GetMapping("/{postId}/comments")
  public ResponseEntity<Object> getListCommentForPost(@PathVariable Long postId, @Valid @ModelAttribute GetCommentRequest getCommentRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.getCommentsByPostId(postId, getCommentRequest));
  }

  @GetMapping("/comments/{commentId}")
  public ResponseEntity<Object> getCommentById(@PathVariable Long commentId){
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.getCommentById(commentId));
  }

//  @GetMapping("/comments")
//  public ResponseEntity<Object> getCommentsByInteractions() {
//    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.getCommentsByInteractions());
//  }


  @GetMapping("/comments/{commentId}/replies")
  public ResponseEntity<Object> getListCommentChildByCommentParentId(@PathVariable Long commentId, @Valid @ModelAttribute GetCommentRequest getCommentRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, commentPostInput.getCommentChildByParentId(commentId, getCommentRequest));
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

//  @PostMapping("/reactions/comments/{c}")
//  public ResponseEntity<Object> addReactionToComment(@PathVariable Long c, @RequestBody @Valid ReactionRequest reactionRequest) {
//    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, reactionCommentInput.handleReactionComment(c, reactionRequest.getReactionType()));
//  }
//
//  @GetMapping("/reactions/comments/{c}")
//  public ResponseEntity<Object> getReactionInComment(@PathVariable Long c) {
//    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, reactionCommentInput.getAllReactionInComment(c));
//  }
}

