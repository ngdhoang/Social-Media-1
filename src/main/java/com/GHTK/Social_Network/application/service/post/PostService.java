package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.ImagePostInput;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements PostPortInput {
  private final ImagePostInput imagePostInput;

  private final AuthPort authPort;
  private final PostPort portPost;
  private final ImagePostPort imagePostPort;
  private final FriendShipPort friendShipPort;
  private final RedisImageTemplatePort redisImageTemplatePort;

  private final PostMapper postMapper;

  @Override
  public List<PostResponse> getPostsByUserId(Long userId) {
    User currentUser = authPort.getUserAuthOrDefaultVirtual();
    validateUserStatus(currentUser.getUserId(), userId);

    PostPort.TAKE_POST_STATUS status;
    if (userId.equals(currentUser.getUserId())) {
      status = PostPort.TAKE_POST_STATUS.ALL;
    } else if (friendShipPort.isFriend(currentUser.getUserId(), userId)) {
      status = PostPort.TAKE_POST_STATUS.FRIEND;
    } else {
      status = PostPort.TAKE_POST_STATUS.PUBLIC;
    }

    List<Post> postList = portPost.findPostsByUserIdAndFriendStatus(userId, status);

    return postList.stream()
            .map(this::mapPostToResponse)
            .toList();
  }


  @Override
  public List<PostResponse> getPostsByInteractions() {
    return null;
  }

  @Override
  public List<PostResponse> getPostsTagMe() {
    User currentUser = authPort.getUserAuth();
    List<Post> postList = portPost.findAllPostTagMeNotBlockAndPrivate(currentUser.getUserId());
    return postList.stream()
            .map(this::mapPostToResponse)
            .collect(Collectors.toList());
  }

  @Override
  public PostResponse getPostByPostId(Long postId) {
    Post post = getPostAndCheckAccess(postId);
    List<ImagePost> imagePosts = sortImagePosts(postId, portPost.findAllImageByPostId(post.getPostId()));
    List<TagUser> tagUserList = portPost.findAllTagUserByPostId(post.getPostId());
    List<User> userTagList = tagUserList.stream().map(t -> authPort.getUserById(t.getTagUserId())).toList();
    return postMapper.postToPostResponse(post, imagePosts, userTagList);
  }

  @Override
  public PostResponse createPost(PostRequest postRequest) {
    User currentUser = authPort.getUserAuth();

    Post post = createNewPost(postRequest, currentUser);
    post.setCreatedAt(new Date());
    Post newPost = portPost.savePost(post);

    List<TagUser> tagUserList = handleTagUsers(postRequest.getTagUserIds(), newPost); // Take tag user list
    List<ImagePost> imagePostEntities = handleImagePosts(postRequest.getPublicIds(), newPost); // Take image list

    portPost.savePost(post);

    imagePostPort.deleteAllImageRedisByTail("_" + currentUser.getUserEmail());
    List<User> userTagList = tagUserList.stream().map(t -> authPort.getUserById(t.getTagUserId())).toList();
    return postMapper.postToPostResponse(newPost, imagePostEntities, userTagList);
  }

  @Override
  public PostResponse updatePost(Long postId, PostRequest postRequest) {
    User currentUser = authPort.getUserAuth();

    Post post = getAndValidatePostCurrentUser(postId, currentUser);
    updatePostDetails(post, postRequest);

    Post newPost = portPost.savePost(post);
    List<TagUser> tagUserList = handleTagUsers(postRequest.getTagUserIds(), newPost);
    List<ImagePost> imagePostList = updateImagePosts(postRequest, newPost);
    portPost.savePost(newPost);

    imagePostPort.deleteAllImageRedisByTail("_" + currentUser.getUserEmail());
    List<User> userTagList = tagUserList.stream().map(t -> authPort.getUserById(t.getTagUserId())).toList();
    return postMapper.postToPostResponse(newPost, imagePostList, userTagList);
  }

  @Override
  public MessageResponse deletePost(Long id) {
    User currentUser = authPort.getUserAuth();
    getAndValidatePostCurrentUser(id, currentUser);

    if (!portPost.deletePostById(id)) {
      throw new CustomException("Don't have permission to delete this post", HttpStatus.FORBIDDEN);
    }
    return MessageResponse.builder().message("Successfully deleted").build();
  }


  private Post createNewPost(PostRequest postRequest, User currentUser) {
    EPostStatus ePostStatusEntity = filterStatusPost(postRequest.getStatus());
    return Post.builder()
            .content(postRequest.getContent())
            .postStatus(ePostStatusEntity)
            .userId(currentUser.getUserId())
            .build();
  }

  private void updatePostDetails(Post post, PostRequest postRequest) {
    EPostStatus ePostStatusEntity = filterStatusPost(postRequest.getStatus());
    post.setPostStatus(ePostStatusEntity);
    post.setContent(postRequest.getContent());
    post.setUpdateAt(new Date());
  }

  private Post getAndValidatePostCurrentUser(Long postId, User currentUser) {
    Post post = portPost.findPostByPostId(postId);
    if (post == null) {
      throw new CustomException("The post does not exist", HttpStatus.NOT_FOUND);
    }
    if (!post.getUserId().equals(currentUser.getUserId())) {
      throw new CustomException("User not permission", HttpStatus.UNAUTHORIZED);
    }
    return post;
  }

  private Post getPostAndCheckAccess(Long postId) {
    Post post = portPost.findPostById(postId);
    if (post == null) {
      throw new CustomException("Post does not exist", HttpStatus.NOT_FOUND);
    }

    User postOwner = authPort.getUserById(post.getUserId());
    validateUserStatus(authPort.getUserAuthOrDefaultVirtual().getUserId(), postOwner.getUserId());

    return post;
  }

  private void validateUserStatus(Long currentUserId, Long userId) {
    User user = authPort.getUserById(userId);
    if (user != null) {
      throw new CustomException("User not found", HttpStatus.NOT_FOUND);
    }
    if (friendShipPort.isBlock(userId, currentUserId) || !user.getIsProfilePublic()) {
      throw new CustomException("You do not have permission to view", HttpStatus.FORBIDDEN);
    }
  }

  private List<TagUser> handleTagUsers(List<Long> tagUserIds, Post post) {
    List<TagUser> tagUserList = getTagUsers(tagUserIds, post);
    portPost.saveAllTagUser(tagUserList);
    return tagUserList;
  }

  private TagUser createTagUser(Long userId, Post post) {
    if (!friendShipPort.isFriend(userId, post.getUserId())) {
      throw new CustomException("User not friend or block", HttpStatus.NOT_FOUND);
    }
    User user = authPort.getUserById(userId);
    TagUser tagUser = TagUser.builder()
            .postId(post.getPostId())
            .userId(user.getUserId())
            .build();
    return portPost.saveTagUser(tagUser);
  }

  private List<ImagePost> handleImagePosts(List<String> publicIds, Post post) {
    List<ImagePost> imagePostEntities = new ArrayList<>();
    List<Long> imagePostSort = new ArrayList<>();
    List<String> keyLoadings = new ArrayList<>();
    String tail = "_" + ImagePostInput.POST_TAIL + "_" + authPort.getUserAuthOrDefaultVirtual().getUserEmail();

    for (String key : publicIds) {
      String fullKey = key + tail;
      if (redisImageTemplatePort.existsByKey(fullKey)) {
        String value = redisImageTemplatePort.findByKey(fullKey);

        if (value.equals(ImagePostInput.VALUE_LOADING)) { // If image loading not completed yet
          keyLoadings.add(fullKey);
          imagePostSort.add(0L); // Add 0 for loading images
          break;
        }

        ImagePost imagePost = new ImagePost(value, new Date(), post.getPostId());
        ImagePost savedImagePost = imagePostPort.saveImagePost(imagePost);
        imagePostEntities.add(savedImagePost);
        imagePostSort.add(savedImagePost.getImagePostId());
        redisImageTemplatePort.deleteByKey(fullKey);
      }
    }

    if (!keyLoadings.isEmpty()) { // Take image again
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      for (String fullKey : keyLoadings) {
        String value = redisImageTemplatePort.findByKey(fullKey);
        if (!value.equals(ImagePostInput.VALUE_LOADING)) {
          ImagePost imagePost = new ImagePost(value, new Date(), post.getPostId());
          ImagePost savedImagePost = imagePostPort.saveImagePost(imagePost);
          imagePostEntities.add(savedImagePost);
          // Replace the corresponding 0 in imagePostSort with the new ID
          int index = imagePostSort.indexOf(0L);
          if (index != -1) {
            imagePostSort.set(index, savedImagePost.getImagePostId());
          }
          redisImageTemplatePort.deleteByKey(fullKey);
        }
      }
    }

    imagePostPort.saveImageSequence(new ImageSequenceDomain(post.getPostId().toString(), imagePostSort));
    imagePostPort.saveAllImagePost(imagePostEntities);
    return imagePostEntities;
  }

  private List<ImagePost> updateImagePosts(PostRequest postRequest, Post post) {
    List<Long> imageIds = postRequest.getImageIds();
    List<String> publicIds = postRequest.getPublicIds();

    int cnt = 0;
    for (int i = 0; i < imageIds.size(); i++) {
      if (imageIds.get(i) == 0) {
        ImagePost imagePost = new ImagePost(redisImageTemplatePort.findByKey(publicIds.get(cnt) + "_" + authPort.getUserAuthOrDefaultVirtual().getUserEmail()), new Date(), post.getPostId());
        ImagePost newImagePost = imagePostPort.saveImagePost(imagePost);
        imageIds.set(i, newImagePost.getImagePostId());
        redisImageTemplatePort.deleteByKey(publicIds.get(cnt) + "_" + authPort.getUserAuthOrDefaultVirtual().getUserEmail());
        cnt++;
      }
      if (cnt > postRequest.getImageIds().size()) {
        break;
      }
    }
    imagePostPort.saveImageSequence(new ImageSequenceDomain(post.getPostId().toString(), imageIds));

    // delete image in database
    List<ImagePost> currentImageIdsInDb = imagePostPort.findAllImagePost(post.getPostId());
    List<ImagePost> imagesNotInList = currentImageIdsInDb.stream()
            .filter(imagePost -> !imageIds.contains(imagePost.getImagePostId()))
            .toList();
    imagePostInput.deleteImagePost(imagesNotInList);

    return sortImagePosts(post.getPostId(), portPost.findAllImageByPostId(post.getPostId()));
  }

  private List<ImagePost> sortImagePosts(Long postId, List<ImagePost> imagePosts) {
    Optional<ImageSequenceDomain> imageSequenceOpt = imagePostPort.findImageSequenceByPostId(postId);

    if (imageSequenceOpt.isEmpty()) {
      return imagePosts;
    }

    List<Long> sortOrder = imageSequenceOpt.get().getListImageSort();

    if (sortOrder == null || sortOrder.isEmpty()) {
      return imagePosts;
    }

    Map<Long, ImagePost> imagePostMap = imagePosts.stream()
            .collect(Collectors.toMap(ImagePost::getImagePostId, Function.identity(), (e1, e2) -> e1));

    return sortOrder.stream()
            .filter(imagePostMap::containsKey)
            .map(imagePostMap::get)
            .collect(Collectors.toList());
  }

  // Mapping string to enum EPostStatus
  private EPostStatus filterStatusPost(String status) {
    return switch (status.toLowerCase()) {
      case "private" -> EPostStatus.PRIVATE;
      case "friend" -> EPostStatus.FRIEND;
      default -> EPostStatus.PUBLIC; // default public
    };
  }

  private List<TagUser> getTagUsers(List<Long> tagUserIds, Post post) {
    return tagUserIds.stream()
            .map(u -> createTagUser(u, post))
            .collect(Collectors.toList());
  }

  private PostResponse mapPostToResponse(Post p) {
    List<ImagePost> imagePostList = portPost.findAllImageByPostId(p.getPostId());
    List<TagUser> tagUserList = portPost.findAllTagUserByPostId(p.getPostId());
    return postMapper.postToPostResponse(p, imagePostList, tagUserList);
  }

}