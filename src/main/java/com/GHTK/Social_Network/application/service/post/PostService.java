package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.PostPortInput;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.post.ImagePostPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.ImageSequence;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatus;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ImagePost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.Post;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.TagUser;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.PostMapper;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.PostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements PostPortInput {
  private final PostPort portPost;

  private final ImagePostPort imagePostPort;

  private final RedisTemplate<String, String> imageRedisTemplate;

  private final FriendShipPort friendShipPort;

  private final AuthPort authenticationRepositoryPort;

  private UserEntity getUserAuth() {
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
              if (!friendShipPort.isFriend(u, post.getUserEntity().getUserId())) {
                throw new CustomException("User not friend or block", HttpStatus.NOT_FOUND);
              }
              UserEntity userEntity = authenticationRepositoryPort.getUserById(u);
              TagUser tagUser = TagUser.builder().post(post).userEntity(userEntity).build();
              portPost.saveTagUser(tagUser);
              tagUserList.add(tagUser);
            }
    );
    return tagUserList;
  }

  @Override
  public PostResponse createPost(PostRequest postRequest) {
    // async
    String tail = "_" + getUserAuth().getUserEmail();
    imagePostPort.deleteImageRedisByPublicId(postRequest.getDeletePublicIds(), tail);

    // take status post
    EPostStatus ePostStatus = filterStatusPost(postRequest.getStatus());

    // Create new post
    Post post = Post.builder()
            .content(postRequest.getContent())
            .postStatus(ePostStatus)
            .userEntity(getUserAuth()).build();

    Post newPost = portPost.savePost(post);

    // Handler tag user
    List<TagUser> tagUserList = new ArrayList<>();
    if (!postRequest.getTagUserIds().isEmpty()) {
      getTagUsers(postRequest.getTagUserIds(), post);
      post.setTagUsers(tagUserList);
      post.setUserEntity(getUserAuth());
    }

    // Sau đó xử lý và lưu ImagePost
    List<String> keys = postRequest.getPublicIds();
    List<ImagePost> imagePosts = new ArrayList<>();
    List<Long> imagePostSort = new ArrayList<>();
    for (String k : keys) {
      if (Boolean.TRUE.equals(imageRedisTemplate.hasKey(k))) {
        String url = imageRedisTemplate.opsForValue().get(k);
        ImagePost imagePost = new ImagePost(url, new Date(), newPost);
        imagePosts.add(imagePostPort.saveImagePost(imagePost));
        imagePostSort.add(imagePost.getImagePostId());
        imageRedisTemplate.delete(k);
      }
    }
    imagePostPort.saveImageSequence(new ImageSequence(post.getPostId(), imagePostSort));

    newPost.setImagePosts(imagePosts);
    newPost.setCreatedAt(new Date());
    newPost = portPost.savePost(newPost);

    return PostMapper.INSTANCE.postToPostResponse(newPost);
  }

  @Override
  public PostResponse updatePost(PostRequest postRequest) {
    imagePostPort.deleteImageRedisByPublicId(postRequest.getDeletePublicIds(), "_" + getUserAuth().getUserEmail());

    UserEntity userEntity = getUserAuth();
    // Check post exist
    Post post = portPost.findPostByPostId(postRequest.getId());
    if (post == null) {
      throw new CustomException("The post does not exist", HttpStatus.NOT_FOUND);
    }
    if (!post.getUserEntity().equals(userEntity)) {
      throw new CustomException("User not permission", HttpStatus.UNAUTHORIZED);
    }

    // Take status post
    EPostStatus ePostStatus = filterStatusPost(postRequest.getStatus());
    post.setPostStatus(ePostStatus);
    post.setContent(postRequest.getContent());
    post.setUpdateAt(new Date());

    // Handler tag user
    List<TagUser> tagUserList = getTagUsers(postRequest.getTagUserIds(), post);
    post.setTagUsers(tagUserList);

    List<Long> imageIds = postRequest.getImageIds();
    List<String> publicIds = postRequest.getPublicIds();

    int cnt = 0;
    for (int i = 0; i < imageIds.size(); i++) {
      if (imageIds.get(i) == 0) {
        if (cnt < publicIds.size()) {
          ImagePost imagePost = new ImagePost(publicIds.get(cnt++), new Date(), post);
          ImagePost newImagePost = imagePostPort.saveImagePost(imagePost);
          imageIds.set(i, newImagePost.getImagePostId());
          imageRedisTemplate.delete(publicIds.get(cnt - 1));
        }
      }
    }

    imagePostPort.saveImageSequence(new ImageSequence(post.getPostId(), imageIds));

    Post newPost = portPost.savePost(post);
    newPost.setImagePosts(sortImagePosts(newPost.getPostId(), newPost.getImagePosts()));
    return PostMapper.INSTANCE.postToPostResponse(newPost);
  }

  @Override
  public MessageResponse deletePost(Long id) {
    UserEntity userEntity = getUserAuth();
    // Check post exist
    Post post = portPost.findPostByPostId(id);
    if (post == null) {
      throw new CustomException("The post does not exist", HttpStatus.NOT_FOUND);
    }
    if (!post.getUserEntity().equals(userEntity)) {
      throw new CustomException("User not permission", HttpStatus.UNAUTHORIZED);
    }

    if (!portPost.deletePostById(id))
      throw new CustomException("Don't have permission to delete this post", HttpStatus.FORBIDDEN);
    return MessageResponse.builder().message("Successfully deleted").build();
  }

  @Override
  public List<PostResponse> getAllPostsByUserId(Long userId) {
    // Check user exist and no private for me or don't block me
    UserEntity userEntity = portPost.findUserById(userId);
    if (userEntity == null || !userEntity.getIsProfilePublic()) {
      throw new CustomException("User does not exist or profile private", HttpStatus.NOT_FOUND);
    }

    // Check block
    if (friendShipPort.isBlock(userId, getUserAuth().getUserId()) && userId.equals(getUserAuth().getUserId())) {
      throw new CustomException("User has blocked", HttpStatus.FORBIDDEN);
    }
    // -----------------

    List<Post> postList = portPost.findAllPostByUser(userEntity);

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

    UserEntity userEntity = portPost.findUserByPost(post);
    if (!userEntity.getIsProfilePublic()) {
      throw new CustomException("User does not exist or profile private", HttpStatus.FORBIDDEN);
    }

    // Check block
    if (friendShipPort.isBlock(post.getUserEntity().getUserId(), getUserAuth().getUserId()) && post.getUserEntity().getUserId().equals(getUserAuth().getUserId())) {
      throw new CustomException("User has blocked", HttpStatus.FORBIDDEN);
    }
    // ----------------------
    List<ImagePost> imagePosts = post.getImagePosts();
    post.setImagePosts(sortImagePosts(postId, imagePosts));

    return PostMapper.INSTANCE.postToPostResponse(post);
  }

  List<ImagePost> sortImagePosts(Long postId, List<ImagePost> imagePosts) {
    List<Long> longList = imagePostPort.findImageSequenceByPostId(postId).getListImageSort();

    Map<Long, ImagePost> imagePostMap = new HashMap<>();
    for (ImagePost imagePost : imagePosts) {
      imagePostMap.put(imagePost.getImagePostId(), imagePost);
    }

    List<ImagePost> sortedImagePosts = new ArrayList<>();
    for (Long imagePostId : longList) {
      if (imagePostMap.containsKey(imagePostId)) {
        sortedImagePosts.add(imagePostMap.get(imagePostId));
      }
    }
    return sortedImagePosts;
  }
}
