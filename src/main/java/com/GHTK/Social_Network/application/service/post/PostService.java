package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.PostPortInput;
import com.GHTK.Social_Network.application.port.output.AuthPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.domain.entity.post.EPostStatus;
import com.GHTK.Social_Network.domain.entity.post.Post;
import com.GHTK.Social_Network.domain.entity.post.TagUser;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.exception.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.PostMapper;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.PostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements PostPortInput {
  private final PostPort portPost;
  private final AuthPort authenticationRepositoryPort;

  private User getUserAuth() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username;

    if (principal instanceof UserDetails) {
      username = ((UserDetails) principal).getUsername();
    } else if (principal instanceof String) {
      username = (String) principal;
    } else {
      throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }

    return authenticationRepositoryPort.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid token"));
  }

  // Handler status post
  private EPostStatus filterStatusPost(String status) {
    EPostStatus ePostStatus = EPostStatus.PUBLIC;
    if (status.equals("private")) {
      ePostStatus = EPostStatus.PRIVATE;
    } else if (status.equals("friend")) {
      ePostStatus = EPostStatus.FRIEND;
    }
    return ePostStatus;
  }

  // Change tagUserDto to tagUserEntity
  private List<TagUser> getTagUsers(List<Long> tagUserIds, Post post) {
    List<TagUser> tagUserList = new ArrayList<>();
    tagUserIds.stream().forEach(
            u -> {
              // Check user is friend and no block
              // true
              User user = new User(); // temp
              TagUser tagUser = TagUser.builder().post(post).user(user).build();
              tagUserList.add(tagUser);
              //false throw error
            }
    );
    return tagUserList;
  }

  @Override
  public PostResponse createPost(PostRequest postRequest) {
    // take status post
    EPostStatus ePostStatus = filterStatusPost(postRequest.getStatus());

    // Create new post
    Post post = Post.builder()
            .content(postRequest.getContent())
            .postStatus(ePostStatus).build();

    // Handler tag user
    List<TagUser> tagUserList = getTagUsers(postRequest.getTagUserIds(), post);
    post.setTagUsers(tagUserList);
    post.setUser(getUserAuth()); // Set relationship

    // Save post into repository
    Post newPost = portPost.savePost(post);
    return PostMapper.INSTANCE.postToPostResponse(newPost);
  }

  @Override
  public PostResponse updatePost(PostRequest postRequest) {
    User user = getUserAuth();
    // Check post exist
    Post post = portPost.findPostByPostIdAndUser(postRequest.getId(), user);
    if (post == null) {
      throw new CustomException("The post does not exist", HttpStatus.NOT_FOUND);
    }

    // Take status post
    EPostStatus ePostStatus = filterStatusPost(postRequest.getStatus());
    post.setPostStatus(ePostStatus);
    post.setContent(postRequest.getContent());
    post.setUpdateAt(new Date());

    // Handler tag user
    List<TagUser> tagUserList = getTagUsers(postRequest.getTagUserIds(), post);
    post.setTagUsers(tagUserList);

    Post newPost = portPost.savePost(post);
    return PostMapper.INSTANCE.postToPostResponse(newPost);
  }

  @Override
  public MessageResponse deletePost(Long id) {
    User user = getUserAuth();
    // Check post exist
    Post post = portPost.findPostByPostIdAndUser(id, user);
    if (post == null) {
      throw new CustomException("The post does not exist", HttpStatus.NOT_FOUND);
    }

    if (!portPost.deletePostById(id))
      throw new CustomException("Don't have permission to delete this post", HttpStatus.FORBIDDEN);
    return MessageResponse.builder().message("Successfully deleted").build();
  }

  @Override
  public List<PostResponse> getAllPostsByUserId(Long userId) {
    // Check user exist and no private for me or don't block me
    User user = portPost.findUserById(userId);
    if (user == null || !user.getIsProfilePublic()) {
      throw new CustomException("User does not exist or profile private", HttpStatus.NOT_FOUND);
    }

    // Check block

    // -----------------

    List<Post> postList = portPost.findAllPostById(user);

    return postList.stream()
            .map(PostMapper.INSTANCE::postToPostResponse)
            .collect(Collectors.toList());
  }

  @Override
  public List<PostResponse> getAllPostsTagMe() {
    List<PostResponse> postResponseList = new ArrayList<>();
    List<Post> postList = portPost.findAllPostTagMe(getUserAuth());
    postList.stream().forEach(
            post -> postResponseList.add(PostMapper.INSTANCE.postToPostResponse(post))
    );
    return postResponseList;
  }

  @Override
  public PostResponse getPostsByPostId(Long postId) {
    // Check post exist, private or block
    Post post = portPost.findPostById(postId);
    if (post == null) {
      throw new CustomException("Post does not exist", HttpStatus.NOT_FOUND);
    }

    User user = portPost.findUserByPost(post);
    if (!user.getIsProfilePublic()) {
      throw new CustomException("User does not exist or profile private", HttpStatus.FORBIDDEN);
    }

    // Check block

    // ----------------------
    return PostMapper.INSTANCE.postToPostResponse(post);
  }
}
