package com.GHTK.Social_Network.application.port.input.post;

import com.GHTK.Social_Network.infrastructure.payload.requests.GetPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.PostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.InteractionResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.PostResponse;

import java.util.List;

public interface PostPortInput {
  List<PostResponse> getPostsByUserId(Long userId, GetPostRequest getPostRequest); // No auth or auth

  List<InteractionResponse> getPostsByInteractions(); // Auth

  List<InteractionResponse> getPostsTagMe(GetPostRequest getPostRequest); // Auth

  PostResponse getPostByPostId(Long postId); // No auth or auth

  PostResponse createPost(PostRequest postRequest); // Auth

  PostResponse updatePost(Long postId, PostRequest postRequest); // Auth

  MessageResponse deletePost(Long id); // Auth
}
