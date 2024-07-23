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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements PostPortInput {
  private final AuthPort authPort;
  private final PostPort portPost;
  private final ImagePostPort imagePostPort;
  private final FriendShipPort friendShipPort;
  private final RedisImageTemplatePort redisImageTemplatePort;

  private final PostMapper postMapper;

  private User getUserAuth() {
    User user = authPort.getUserAuth();
    return user == null ? User.builder().userId(0L).build() : user;
  }

  private EPostStatus filterStatusPost(String status) {
    return switch (status.toLowerCase()) {
      case "private" -> EPostStatus.PRIVATE;
      case "friend" -> EPostStatus.FRIEND;
      default -> EPostStatus.PUBLIC;
    };
  }

  private List<TagUser> getTagUsers(List<Long> tagUserIds, Post post) {
    return tagUserIds.stream()
            .map(u -> createTagUser(u, post))
            .collect(Collectors.toList());
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

  @Override
  public PostResponse createPost(PostRequest postRequest) {
    User currentUser = this.getUserAuth();

    Post post = createNewPost(postRequest, currentUser);
    post.setCreatedAt(new Date());
    Post newPost = portPost.savePost(post);
    List<TagUser> tagUserList = handleTagUsers(postRequest.getTagUserIds(), newPost);
    List<ImagePost> imagePostEntities = handleImagePosts(postRequest.getPublicIds(), newPost);

    portPost.savePost(post);

    deleteRedisImages(postRequest.getDeletePublicIds(), currentUser.getUserEmail());

    return postMapper.postToPostResponse(newPost, imagePostEntities, tagUserList);
  }

  private void deleteRedisImages(List<String> deletePublicIds, String userEmail) {
    imagePostPort.deleteImageRedisByPublicId(deletePublicIds, "_" + userEmail);
  }

  private Post createNewPost(PostRequest postRequest, User currentUser) {
    EPostStatus ePostStatusEntity = filterStatusPost(postRequest.getStatus());
    return Post.builder()
            .content(postRequest.getContent())
            .postStatus(ePostStatusEntity)
            .userId(currentUser.getUserId())
            .build();
  }

  private List<TagUser> handleTagUsers(List<Long> tagUserIds, Post post) {
    List<TagUser> tagUserList = getTagUsers(tagUserIds, post);
    portPost.saveAllTagUser(tagUserList);
    return tagUserList;
  }

  private List<ImagePost> handleImagePosts(List<String> publicIds, Post post) {
    List<ImagePost> imagePostEntities = new ArrayList<>();
    List<Long> imagePostSort = new ArrayList<>();

    for (String key : publicIds) {
      key += "_" + this.getUserAuth().getUserEmail();
      if (redisImageTemplatePort.existsByKey(key)) {
        String url = redisImageTemplatePort.findByKey(key);
        ImagePost imagePost = new ImagePost(url, new Date(), post.getPostId());
        ImagePost savedImagePost = imagePostPort.saveImagePost(imagePost);
        imagePostEntities.add(savedImagePost);
        imagePostSort.add(savedImagePost.getImagePostId());
        redisImageTemplatePort.deleteByKey(key);
      }
    }

    imagePostPort.saveImageSequence(new ImageSequenceDomain(post.getPostId().toString(), imagePostSort));
    imagePostPort.saveAllImagePost(imagePostEntities);
    return imagePostEntities;
  }

  @Override
  public PostResponse updatePost(PostRequest postRequest) {
    User currentUser = this.getUserAuth();

    Post post = getAndValidatePost(postRequest.getId(), currentUser);
    updatePostDetails(post, postRequest);

    Post newPost = portPost.savePost(post);
    List<TagUser> tagUserList = handleTagUsers(postRequest.getTagUserIds(), newPost);
    List<ImagePost> imagePostList = updateImagePosts(postRequest, newPost);
    portPost.savePost(newPost);


    deleteRedisImages(postRequest.getDeletePublicIds(), currentUser.getUserEmail());
    return postMapper.postToPostResponse(newPost, imagePostList, tagUserList);
  }

  private Post getAndValidatePost(Long postId, User currentUser) {
    Post post = portPost.findPostByPostId(postId);
    if (post == null) {
      throw new CustomException("The post does not exist", HttpStatus.NOT_FOUND);
    }
    if (!post.getUserId().equals(currentUser.getUserId())) {
      throw new CustomException("User not permission", HttpStatus.UNAUTHORIZED);
    }
    return post;
  }

  private void updatePostDetails(Post post, PostRequest postRequest) {
    EPostStatus ePostStatusEntity = filterStatusPost(postRequest.getStatus());
    post.setPostStatus(ePostStatusEntity);
    post.setContent(postRequest.getContent());
    post.setUpdateAt(new Date());
  }

  private List<ImagePost> updateImagePosts(PostRequest postRequest, Post post) {
    List<Long> imageIds = postRequest.getImageIds();
    List<String> publicIds = postRequest.getPublicIds();

    int cnt = 0;
    for (int i = 0; i < imageIds.size(); i++) {
      if (imageIds.get(i) == 0) {
        ImagePost imagePost = new ImagePost(redisImageTemplatePort.findByKey(publicIds.get(cnt) + "_" + getUserAuth().getUserEmail()), new Date(), post.getPostId());
        ImagePost newImagePost = imagePostPort.saveImagePost(imagePost);
        imageIds.set(i, newImagePost.getImagePostId());
        redisImageTemplatePort.deleteByKey(publicIds.get(cnt) + "_" + getUserAuth().getUserEmail());
        cnt++;
      }
      if (cnt > postRequest.getImageIds().size()) {
        break;
      }
    }
    System.out.println(imageIds);

    imagePostPort.saveImageSequence(new ImageSequenceDomain(post.getPostId().toString(), imageIds));
    return sortImagePosts(post.getPostId(), portPost.findAllImageByPostId(post.getPostId()));
  }

  @Override
  public MessageResponse deletePost(Long id) {
    User currentUser = this.getUserAuth();
    getAndValidatePost(id, currentUser);

    if (!portPost.deletePostById(id)) {
      throw new CustomException("Don't have permission to delete this post", HttpStatus.FORBIDDEN);
    }
    return MessageResponse.builder().message("Successfully deleted").build();
  }

  @Override
  public List<PostResponse> getAllPostsByUserId(Long userId) {
//    List<Post> postList = new ArrayList<>();
//    if (getUserAuth().getUserId() != 0) {
    User targetUser = getUserAndCheckAccess(userId);
    List<Post> postList = portPost.findAllPostByUserId(targetUser.getUserId());
//    } else {
//      User u = authPort.getUserById(userId);
//      if (u == null || !u.getIsProfilePublic()) {
//        throw new CustomException("User not found or private", HttpStatus.NOT_FOUND);
//      }
//      postList = portPost.findAllPostByUserId(userId);
//    }

    return postList.stream()
            .map(this::mapPostToResponse)
            .collect(Collectors.toList());
  }

  private User getUserAndCheckAccess(Long userId) {
    User targetUser = authPort.getUserById(userId);
    if (targetUser == null || !targetUser.getIsProfilePublic()) {
      throw new CustomException("User does not exist or profile private", HttpStatus.NOT_FOUND);
    }

    User currentUser = this.getUserAuth();
    if (friendShipPort.isBlock(userId, currentUser.getUserId()) && !userId.equals(currentUser.getUserId())) {
      throw new CustomException("User has blocked", HttpStatus.FORBIDDEN);
    }

    return targetUser;
  }

  @Override
  public List<PostResponse> getAllPostsTagMe() {
    User currentUser = this.getUserAuth();
    List<Post> postList = portPost.findAllPostTagMe(currentUser.getUserId());
    return postList.stream()
            .map(this::mapPostToResponse)
            .collect(Collectors.toList());
  }

  private PostResponse mapPostToResponse(Post p) {
    List<ImagePost> imagePostList = portPost.findAllImageByPostId(p.getPostId());
    List<TagUser> tagUserList = portPost.findAllTagUserByPostId(p.getPostId());
    return postMapper.postToPostResponse(p, imagePostList, tagUserList);
  }

  @Override
  public PostResponse getPostsByPostId(Long postId) {
    Post post = getPostAndCheckAccess(postId);
    List<ImagePost> imagePosts = sortImagePosts(postId, portPost.findAllImageByPostId(post.getPostId()));
    List<TagUser> tagUserList = portPost.findAllTagUserByPostId(post.getPostId());
    return postMapper.postToPostResponse(post, imagePosts, tagUserList);
  }

  private Post getPostAndCheckAccess(Long postId) {
    Post post = portPost.findPostById(postId);
    if (post == null) {
      throw new CustomException("Post does not exist", HttpStatus.NOT_FOUND);
    }

    User postOwner = authPort.getUserById(post.getUserId());
    if (!postOwner.getIsProfilePublic()) {
      throw new CustomException("User does not exist or profile private", HttpStatus.FORBIDDEN);
    }

    User currentUser = this.getUserAuth();
    if (friendShipPort.isBlock(post.getUserId(), currentUser.getUserId()) && !post.getUserId().equals(currentUser.getUserId())) {
      throw new CustomException("User has blocked", HttpStatus.FORBIDDEN);
    }

    return post;
  }

  private List<ImagePost> sortImagePosts(Long postId, List<ImagePost> imagePosts) {
    Optional<ImageSequenceDomain> imageSequenceOpt = imagePostPort.findImageSequenceByPostId(postId);
    System.out.println(imageSequenceOpt);

    if (imageSequenceOpt.isEmpty()) {
      return imagePosts;
    }

    List<Long> sortOrder = imageSequenceOpt.get().getListImageSort();

    if (sortOrder == null || sortOrder.isEmpty()) {
      return imagePosts;
    }

    Map<Long, ImagePost> imagePostMap = imagePosts.stream()
            .collect(Collectors.toMap(ImagePost::getImagePostId, Function.identity(), (e1, e2) -> e1));

    List<ImagePost> sortedPosts = sortOrder.stream()
            .filter(imagePostMap::containsKey)
            .map(imagePostMap::get)
            .collect(Collectors.toList());

    return sortedPosts;
  }
}