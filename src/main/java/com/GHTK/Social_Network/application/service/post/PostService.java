package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.PostPortInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.post.ImagePostPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.post.EPostStatus;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.TagUser;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.MapperEntity.UserMapper;
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


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements PostPortInput {
  private final PostPort portPost;
  private final ImagePostPort imagePostPort;
  private final FriendShipPort friendShipPort;
  private final AuthPort authPort;

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

    return authPort.findByEmail(username)
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
              if (!friendShipPort.isFriend(u, post.getUserId())) {
                throw new CustomException("User not friend or block", HttpStatus.NOT_FOUND);
              }
              User user = authPort.getUserById(u);
              TagUser tagUser = TagUser.builder().postId(post.getPostId()).userId(user.getUserId()).build();
              portPost.saveTagUser();
              tagUserList.add(tagUser);
            }
    );
    return tagUserList;
  }

  @Override
  public PostResponse createPost(PostRequest postRequest) {
    // async
    imagePostPort.deleteImageRedisByPublicId(postRequest.getDeletePublicIds());

    // take status post
    EPostStatus ePostStatus = filterStatusPost(postRequest.getStatus());

    // Create new post
    Post postEntity = PostEntity.builder()
            .content(postRequest.getContent())
            .postStatus(ePostStatus)
            .userEntity(getUserAuth()).build();

    PostEntity newPostEntity = portPost.savePost(postEntity);

    // Handler tag user
    List<TagUser> tagUserList = new ArrayList<>();
    if (!postRequest.getTagUserIds().isEmpty()) {
      getTagUsers(postRequest.getTagUserIds(), postEntity);
      postEntity.setTagUsers(tagUserList);
      postEntity.setUserEntity(getUserAuth());
    }

    // Sau đó xử lý và lưu ImagePost
    List<String> keys = postRequest.getPublicIds();
    List<ImagePost> imagePosts = new ArrayList<>();
    List<Long> imagePostSort = new ArrayList<>();
    for (String k : keys) {
      if (Boolean.TRUE.equals(imageRedisTemplate.hasKey(k))) {
        String url = imageRedisTemplate.opsForValue().get(k);
        ImagePost imagePost = new ImagePost(url, new Date(), newPostEntity);
        imagePosts.add(imagePostPort.saveImagePost(imagePost));
        imagePostSort.add(imagePost.getImagePostId());
        imageRedisTemplate.delete(k);
      }
    }
    imagePostPort.saveImageSequence(new ImageSequence(postEntity.getPostId(), imagePostSort));

    newPostEntity.setImagePosts(imagePosts);
    newPostEntity.setCreatedAt(new Date());
    newPostEntity = portPost.savePost(newPostEntity);

    return PostMapper.INSTANCE.postToPostResponse(newPostEntity);
  }

  @Override
  public PostResponse updatePost(PostRequest postRequest) {
    imagePostPort.deleteImageRedisByPublicId(postRequest.getDeletePublicIds());

    UserEntity userEntity = getUserAuth();
    // Check post exist
    PostEntity postEntity = portPost.findPostByPostId(postRequest.getId());
    if (postEntity == null) {
      throw new CustomException("The post does not exist", HttpStatus.NOT_FOUND);
    }
    if (!postEntity.getUserEntity().equals(userEntity)) {
      throw new CustomException("User not permission", HttpStatus.UNAUTHORIZED);
    }

    // Take status post
    EPostStatus ePostStatus = filterStatusPost(postRequest.getStatus());
    postEntity.setPostStatus(ePostStatus);
    postEntity.setContent(postRequest.getContent());
    postEntity.setUpdateAt(new Date());

    // Handler tag user
    List<TagUser> tagUserList = getTagUsers(postRequest.getTagUserIds(), postEntity);
    postEntity.setTagUsers(tagUserList);

    List<Long> imageIds = postRequest.getImageIds();
    List<String> publicIds = postRequest.getPublicIds();

    int cnt = 0;
    for (int i = 0; i < imageIds.size(); i++) {
      if (imageIds.get(i) == 0) {
        if (cnt < publicIds.size()) {
          ImagePost imagePost = new ImagePost(publicIds.get(cnt++), new Date(), postEntity);
          ImagePost newImagePost = imagePostPort.saveImagePost(imagePost);
          imageIds.set(i, newImagePost.getImagePostId());
          imageRedisTemplate.delete(publicIds.get(cnt - 1));
        }
      }
    }

    imagePostPort.saveImageSequence(new ImageSequence(postEntity.getPostId(), imageIds));

    PostEntity newPostEntity = portPost.savePost(postEntity);
    newPostEntity.setImagePosts(sortImagePosts(newPostEntity.getPostId(), newPostEntity.getImagePosts()));
    return PostMapper.INSTANCE.postToPostResponse(newPostEntity);
  }

  @Override
  public MessageResponse deletePost(Long id) {
    UserEntity userEntity = getUserAuth();
    // Check post exist
    PostEntity postEntity = portPost.findPostByPostId(id);
    if (postEntity == null) {
      throw new CustomException("The post does not exist", HttpStatus.NOT_FOUND);
    }
    if (!postEntity.getUserEntity().equals(userEntity)) {
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
    if (friendShipPort.isBlock(userId, getUserAuth().getUserId())) {
      throw new CustomException("User has blocked", HttpStatus.FORBIDDEN);
    }
    // -----------------

    List<PostEntity> postEntityList = portPost.findAllPostByUser(userEntity);

    return postEntityList.stream()
            .map(PostMapper.INSTANCE::postToPostResponse)
            .collect(Collectors.toList());
  }

  @Override
  public List<PostResponse> getAllPostsTagMe() {
    List<PostResponse> postResponseList = new ArrayList<>();
    List<PostEntity> postEntityList = portPost.findAllPostTagMe(getUserAuth());
    postEntityList.stream().forEach(
            post -> postResponseList.add(PostMapper.INSTANCE.postToPostResponse(post))
    );
    return postResponseList;
  }

  @Override
  public PostResponse getPostsByPostId(Long postId) {
    // Check post exist, private or block
    PostEntity postEntity = portPost.findPostById(postId);
    if (postEntity == null) {
      throw new CustomException("Post does not exist", HttpStatus.NOT_FOUND);
    }

    UserEntity userEntity = portPost.findUserByPost(postEntity);
    if (!userEntity.getIsProfilePublic()) {
      throw new CustomException("User does not exist or profile private", HttpStatus.FORBIDDEN);
    }

    // Check block
    if (friendShipPort.isBlock(postEntity.getUserEntity().getUserId(), getUserAuth().getUserId())) {
      throw new CustomException("User has blocked", HttpStatus.FORBIDDEN);
    }
    // ----------------------
    List<ImagePost> imagePosts = postEntity.getImagePosts();
    postEntity.setImagePosts(sortImagePosts(postId, imagePosts));

    return PostMapper.INSTANCE.postToPostResponse(postEntity);
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
