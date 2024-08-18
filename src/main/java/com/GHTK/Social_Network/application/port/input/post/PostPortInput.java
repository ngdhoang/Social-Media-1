package com.GHTK.Social_Network.application.port.input.post;

import com.GHTK.Social_Network.infrastructure.payload.requests.GetPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.PostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.InteractionResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.PostResponse;

import java.util.List;

public interface PostPortInput {
  List<PostResponse> getPostsByUserId(Long userId, GetPostRequest getPostRequest);

  List<InteractionResponse> getPostsByInteractions();

  List<InteractionResponse> getPostsTagMe(GetPostRequest getPostRequest);

  PostResponse getPostByPostId(Long postId);

  PostResponse createPost(PostRequest postRequest);

  PostResponse updatePost(Long postId, PostRequest postRequest);

  MessageResponse deletePost(Long id);
}
