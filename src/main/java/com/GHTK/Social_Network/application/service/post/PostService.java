package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.PostPortInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.post.ImagePostPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.application.port.output.post.RedisImageTemplatePort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.domain.model.collection.ImageSequenceDomain;
import com.GHTK.Social_Network.domain.model.post.EPostStatus;
import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.TagUser;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.PostMapper;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.PostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

  private final RedisImageTemplatePort imageRedisTemplatePort;

  // Handler status post
  private EPostStatus filterStatusPost(String status) {
    EPostStatus ePostStatusEntity = EPostStatus.PUBLIC;
    if (status.equals("private")) {
      ePostStatusEntity = EPostStatus.PRIVATE;
    } else if (status.equals("friend")) {
      ePostStatusEntity = EPostStatus.FRIEND;
    }
    return ePostStatusEntity;
  }

  // Change tagUserDto to tagUser
  private List<TagUser> getTagUsers(List<Long> tagUserIds, Post post) {
    List<TagUser> tagUserList = new ArrayList<>();
    tagUserIds.stream().forEach(
            u -> {
              if (!friendShipPort.isFriend(u, post.getUserId())) {
                throw new CustomException("User not friend or block", HttpStatus.NOT_FOUND);
              }
              User user = authPort.getUserById(u);
              TagUser tagUser = TagUser.builder().postId(post.getPostId()).userId(user.getUserId()).build();
              portPost.saveTagUser(tagUser);
              tagUserList.add(tagUser);
            }
    );
    return tagUserList;
  }

  @Override
  public PostResponse createPost(PostRequest postRequest) {
    // async
    String tail = "_" + authPort.getUserAuth().getUserEmail();
    imagePostPort.deleteImageRedisByPublicId(postRequest.getDeletePublicIds(), tail);

    // take status post
    EPostStatus ePostStatusEntity = filterStatusPost(postRequest.getStatus());

    // Create new post
    Post post = Post.builder()
            .content(postRequest.getContent())
            .postStatus(ePostStatusEntity)
            .userId(authPort.getUserAuth().getUserId()).build();

    Post newPost = portPost.savePost(post);

    // Handler tag user
    List<TagUser> tagUserList = new ArrayList<>();
    if (!postRequest.getTagUserIds().isEmpty()) {
      getTagUsers(postRequest.getTagUserIds(), post);
//      post.setTagUserEntities(tagUserList);
      portPost.saveAllTagUser(tagUserList);
      post.setUserId(authPort.getUserAuth().getUserId());
    }

    // Sau đó xử lý và lưu ImagePost
    List<String> keys = postRequest.getPublicIds();
    List<ImagePost> imagePostEntities = new ArrayList<>();
    List<Long> imagePostSort = new ArrayList<>();
    for (String k : keys) {
      if (Boolean.TRUE.equals(imageRedisTemplatePort.findByKey(k))) {
        String url = imageRedisTemplatePort.findByKey(k);
        ImagePost imagePost = new ImagePost(url, new Date(), newPost.getPostId());
        imagePostEntities.add(imagePostPort.saveImagePost(imagePost));
        imagePostSort.add(imagePost.getImagePostId());
        imageRedisTemplatePort.deleteByKey(k);
      }
    }
    imagePostPort.saveImageSequence(new ImageSequenceDomain(post.getPostId(), imagePostSort));

//    newPost.setImagePostEntities(imagePostEntities);
    imagePostPort.saveAllImagePost(imagePostEntities);
    newPost.setCreatedAt(new Date());
    newPost = portPost.savePost(newPost);

    return PostMapper.INSTANCE.postToPostResponse(newPost);
  }

  @Override
  public PostResponse updatePost(PostRequest postRequest) {
    User userEntity = authPort.getUserAuth();

    imagePostPort.deleteImageRedisByPublicId(postRequest.getDeletePublicIds(), "_" + userEntity.getUserEmail());

    // Check post exist
    Post post = portPost.findPostByPostId(postRequest.getId());
    if (post == null) {
      throw new CustomException("The post does not exist", HttpStatus.NOT_FOUND);
    }
    if (!post.getUserId().equals(userEntity.getUserId())) {
      throw new CustomException("User not permission", HttpStatus.UNAUTHORIZED);
    }

    // Take status post
    EPostStatus ePostStatusEntity = filterStatusPost(postRequest.getStatus());
    post.setPostStatus(ePostStatusEntity);
    post.setContent(postRequest.getContent());
    post.setUpdateAt(new Date());

    // Handler tag user
    List<TagUser> tagUserList = getTagUsers(postRequest.getTagUserIds(), post);
//    post.setTagUserEntities(tagUserList);
    portPost.saveAllTagUser(tagUserList);

    List<Long> imageIds = postRequest.getImageIds();
    List<String> publicIds = postRequest.getPublicIds();

    int cnt = 0;
    for (int i = 0; i < imageIds.size(); i++) {
      if (imageIds.get(i) == 0) {
        if (cnt < publicIds.size()) {
          ImagePost imagePost = new ImagePost(publicIds.get(cnt++), new Date(), post.getPostId());
          ImagePost newImagePost = imagePostPort.saveImagePost(imagePost);
          imageIds.set(i, newImagePost.getImagePostId());
          imageRedisTemplatePort.deleteByKey(publicIds.get(cnt - 1));
        }
      }
    }

    imagePostPort.saveImageSequence(new ImageSequenceDomain(post.getPostId(), imageIds));

    Post newPost = portPost.savePost(post);
    newPost.setImagePostEntities(sortImagePosts(newPost.getPostId(), newPost.getImagePostEntities()));
    return PostMapper.INSTANCE.postToPostResponse(newPost);
  }

  @Override
  public MessageResponse deletePost(Long id) {
    User userEntity = authPort.getUserAuth();
    // Check post exist
    Post post = portPost.findPostByPostId(id);
    if (post == null) {
      throw new CustomException("The post does not exist", HttpStatus.NOT_FOUND);
    }
    if (!post.getUserId().equals(userEntity.getUserId())) {
      throw new CustomException("User not permission", HttpStatus.UNAUTHORIZED);
    }

    if (!portPost.deletePostById(id))
      throw new CustomException("Don't have permission to delete this post", HttpStatus.FORBIDDEN);
    return MessageResponse.builder().message("Successfully deleted").build();
  }

  @Override
  public List<PostResponse> getAllPostsByUserId(Long userId) {
    // Check user exist and no private for me or don't block me
    User userEntity = portPost.findUserById(userId);
    if (userEntity == null || !userEntity.getIsProfilePublic()) {
      throw new CustomException("User does not exist or profile private", HttpStatus.NOT_FOUND);
    }

    // Check block
    User myUser = authPort.getUserAuth();
    if (friendShipPort.isBlock(userId, myUser.getUserId()) && userId.equals(myUser.getUserId())) {
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
    List<Post> postList = portPost.findAllPostTagMe(authPort.getUserAuth());
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
    User myUser = authPort.getUserAuth();
    if (friendShipPort.isBlock(post.getUserId(), myUser.getUserId()) && post.getUserId().equals(myUser.getUserId())) {
      throw new CustomException("User has blocked", HttpStatus.FORBIDDEN);
    }
    // ----------------------
    List<ImagePost> imagePosts = post.getImagePostEntities();
    post.setImagePostEntities(sortImagePosts(postId, imagePostEntities));

    return PostMapper.INSTANCE.postToPostResponse(post);
  }

  List<ImagePost> sortImagePosts(Long postId, List<ImagePost> imagePosts) {
    List<Long> longList = imagePostPort.findImageSequenceByPostId(postId).getListImageSort();

    Map<Long, ImagePost> imagePostMap = new HashMap<>();
    for (ImagePost imagePost : imagePosts) {
      imagePostMap.put(imagePost.getImagePostId(), imagePost);
    }

    List<ImagePost> sortedImagePostEntities = new ArrayList<>();
    for (Long imagePostId : longList) {
      if (imagePostMap.containsKey(imagePostId)) {
        sortedImagePostEntities.add(imagePostMap.get(imagePostId));
      }
    }
    return sortedImagePostEntities;
  }
}
