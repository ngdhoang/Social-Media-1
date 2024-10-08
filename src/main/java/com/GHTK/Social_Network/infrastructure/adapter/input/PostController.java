package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.post.ImagePostInput;
import com.GHTK.Social_Network.application.port.input.post.PostPortInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.PostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
  private final PostPortInput postService;
  private final ImagePostInput imagePostInput;

  @GetMapping
  public ResponseEntity<Object> getAllPostsRecommend() {
    return null;
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<Object> getPostsByUser(@PathVariable Long userId, @Valid @ModelAttribute GetPostRequest getPostRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, postService.getPostsByUserId(userId, getPostRequest));
  }

  @GetMapping("/interactions")
  public ResponseEntity<Object> getPostsByInteractions() {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, postService.getPostsByInteractions());
  }

  @GetMapping("/tags")
  public ResponseEntity<Object> getPostsWithUserTag(@Valid @ModelAttribute GetPostRequest getPostRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, postService.getPostsTagMe(getPostRequest));
  }

  @GetMapping("/{postId}")
  public ResponseEntity<Object> getPostById(@PathVariable Long postId) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, postService.getPostByPostId(postId));
  }

  @PostMapping
  public ResponseEntity<Object> createPost(@RequestBody @Valid PostRequest postRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.CREATED, postService.createPost(postRequest));
  }

  @PutMapping("/{postId}")
  public ResponseEntity<Object> updatePost(
          @PathVariable Long postId,
          @RequestBody @Valid PostRequest postRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, postService.updatePost(postId, postRequest));
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<Object> deletePost(@PathVariable Long postId) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, postService.deletePost(postId));
  }

  @PostMapping("/images")
  public ResponseEntity<Object> uploadImage(@RequestParam("image") @Valid @NotNull(message = "Background file cannot be null") MultipartFile image) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, imagePostInput.createImage(image, ImagePostInput.POST_TAIL));
  }
}