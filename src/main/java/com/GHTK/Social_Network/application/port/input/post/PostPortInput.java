package com.GHTK.Social_Network.application.port.input.post;

import com.GHTK.Social_Network.infrastructure.payload.requests.post.PostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.PostResponse;

import java.util.List;

public interface PostPortInput {
  PostResponse createPost(PostRequest postRequest);

  PostResponse updatePost(PostRequest postRequest);

  MessageResponse deletePost(Long id);

  List<PostResponse> getAllPostsByUserId(Long userId);

  List<PostResponse> getAllPostsTagMe();

  PostResponse getPostsByPostId(Long postId);
}
