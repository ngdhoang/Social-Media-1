package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.post.ImagePostInput;
import com.GHTK.Social_Network.application.port.input.post.PostPortInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.CreateImageRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.PostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
  private final PostPortInput postService;

  private final ImagePostInput imagePostInput;

  @PostMapping("/create")
  public ResponseEntity<Object> create(@RequestBody @Valid PostRequest postRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, postService.createPost(postRequest));
  }

  @PostMapping("/up-image")
  public ResponseEntity<Object> upImage(@ModelAttribute @Valid CreateImageRequest request) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, imagePostInput.createImage(request));
  }

  @PostMapping("/delete-image")
  public ResponseEntity<Object> deleteImage(@RequestParam String p) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, imagePostInput.deleteImageInRedis(p));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> getPostById(@PathVariable Long id) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, postService.getPostsByPostId(id));
  }

  @GetMapping("")
  public ResponseEntity<Object> getPostByUser(@RequestParam Long u) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, postService.getAllPostsByUserId(u));
  }

  @PostMapping("/delete/{id}")
  public ResponseEntity<Object> deletePostById(@PathVariable Long id) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, postService.deletePost(id));
  }

  @PostMapping("/update")
  public ResponseEntity<Object> updatePost(@RequestBody @Valid PostRequest postRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, postService.updatePost(postRequest));
  }
}
