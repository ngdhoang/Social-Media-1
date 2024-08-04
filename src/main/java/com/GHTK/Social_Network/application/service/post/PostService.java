package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.ImagePostInput;
import com.GHTK.Social_Network.application.port.input.post.PostPortInput;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.post.ImagePostPort;
import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.application.port.output.post.ReactionPostPort;
import com.GHTK.Social_Network.application.port.output.post.RedisImageTemplatePort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.collection.ImageSequence;
import com.GHTK.Social_Network.domain.model.post.*;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.PostMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.UserMapper;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.PostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.InteractionResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final ReactionPostPort reactionPostPort;

    private final PostMapper postMapper;
    private final UserMapper userMapper;

    @Override
    public List<PostResponse> getPostsByUserId(Long userId, GetPostRequest getPostRequest) {
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

        List<Long> blockIds = friendShipPort.getListBlockBoth(userId);
        List<Post> postList = portPost.findPostsByUserIdAndFriendStatus(userId, status, blockIds, getPostRequest);

        return postList.stream()
                .map(this::mapPostToResponse)
                .toList();
    }


    //Lấy những bài viết mình đã bình luận
    //Lấy những bài viết mình thả reaction
    // Xem được thông báo những bài bị block nhưng không xem rõ được
    @Override
    public List<InteractionResponse> getPostsByInteractions() {
        User currentUser = authPort.getUserAuth();
        List<Post> postList = portPost.findPostsWithUserInteractions(currentUser.getUserId());
        return generateInteractionPost(postList, currentUser);
    }

    //Lấy những bài viết mình đã được tag tên
    // Xem được thông báo những bài bị block nhưng không xem rõ được
    @Override
    public List<InteractionResponse> getPostsTagMe(GetPostRequest getPostRequest) {
        User currentUser = authPort.getUserAuth();

        List<Long> blockIds = friendShipPort.getListBlockBoth(currentUser.getUserId());
        List<Post> postList = portPost.findPostsTagMe(currentUser.getUserId(), blockIds, getPostRequest);

        return generateInteractionPost(postList, currentUser);
    }

    @Override
    public PostResponse getPostByPostId(Long postId) {
        Post post = getPostAndCheckAccess(postId);
        if (post.getPostStatus().equals(EPostStatus.FRIEND) && !friendShipPort.isFriend(post.getUserId(), authPort.getUserAuthOrDefaultVirtual().getUserId())){
            throw new CustomException("Post not found", HttpStatus.NOT_FOUND);
        }
        if (friendShipPort.isBlock(post.getUserId(), authPort.getUserAuthOrDefaultVirtual().getUserId()) || post.getPostStatus().equals(EPostStatus.PRIVATE)) {
            throw new CustomException("Post not found", HttpStatus.NOT_FOUND);
        }
        List<ImagePost> imagePosts = sortImagePosts(postId, portPost.getListImageByPostId(post.getPostId()));
        List<Long> blockIds = friendShipPort.getListBlockBoth(post.getUserId());
        List<TagUser> tagUserList = portPost.getListTagUserByPostId(post.getPostId(), blockIds);
        List<User> userTagList = tagUserList.stream().map(t -> authPort.getUserById(t.getTagUserId())).toList();
        return postMapper.postToPostResponse(post, imagePosts, userTagList);
    }

    @Override
    public PostResponse createPost(PostRequest postRequest) {
        User currentUser = authPort.getUserAuth();

        Post post = createNewPost(postRequest, currentUser);
        post.setCreateAt(LocalDate.now());
        Post newPost = portPost.savePost(post);

        List<TagUser> tagUserList = handleTagUsers(postRequest.getTagUserIds(), newPost); // Take tag user list
        List<ImagePost> imagePostEntities = handleImagePosts(postRequest.getPublicIds(), newPost); // Take image list

        portPost.savePost(post);

        imagePostPort.deleteAllImageRedisByTail(ImagePostInput.POST_TAIL + currentUser.getUserEmail());
        List<User> userTagList = tagUserList.stream().map(t -> authPort.getUserById(t.getTagUserId())).toList();
        return postMapper.postToPostResponse(newPost, imagePostEntities, userTagList);
    }

    @Override
    public PostResponse updatePost(Long postId, PostRequest postRequest) {
        User currentUser = authPort.getUserAuth();

        Post post = getAndValidatePostCurrentUser(postId, currentUser);
        updatePostDetails(post, postRequest);
        List<Long> blockIds = new ArrayList<>();
//        List<Long> previousTagUserList = portPost.getListTagUserIdByPostId(post.getPostId(), blockIds);
        List<TagUser> previousTagUserList = portPost.getListTagUserByPostId(postId, blockIds);
        Post newPost = portPost.savePost(post);
        List<TagUser> tagUserList = handleUpdateTagUsers(postRequest.getTagUserIds(), previousTagUserList, newPost);
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


    private List<InteractionResponse> generateInteractionPost(List<Post> postList, User currentUser) {
        String role = "POST";
        List<InteractionResponse> interactionResponseList = new ArrayList<>();
        for (Post post : postList) {
            List<ImagePost> imagePosts = imagePostPort.findAllImagePost(post.getPostId());
            ImagePost imagePost = imagePosts.isEmpty() ? null : imagePosts.get(0);

            String content = "You do not have sufficient permissions to view this content.";
            String imageUrl = "";
            if (!friendShipPort.isBlock(post.getUserId(), authPort.getUserAuth().getUserId())) {
                content = post.getContent();
                imageUrl = imagePost == null ? null : imagePost.getImageUrl();
            }
            ReactionPost reactionPost = reactionPostPort.findByPostIdAndUserID(post.getPostId(), currentUser.getUserId());
            EReactionType reactionType = (reactionPost != null) ? reactionPost.getReactionType() : null;
            InteractionResponse interactionResponse = InteractionResponse.builder()
                    .roleId(post.getPostId())
                    .role(role)
                    .owner(
                            userMapper.userToUserBasicDto(
                                    authPort.getUserById(post.getUserId())
                            )
                    )
                    .reactionType(reactionType)
                    .content(content)
                    .image(imageUrl)
                    .createAt(post.getCreateAt())
                    .updateAt(post.getUpdateAt())
                    .build();
            interactionResponseList.add(interactionResponse);
        }
        return interactionResponseList;
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
        post.setUpdateAt(LocalDate.now());
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
        if (user == null) {
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

    private List<TagUser> handleUpdateTagUsers(List<Long> tagUserIds, List<TagUser> previousTagUserList, Post post) {
        List<TagUser> removeTagUserList = previousTagUserList.stream()
                .filter(t -> !tagUserIds.contains(t.getTagUserId()))
                .collect(Collectors.toList());

        List<Long> addTagUserList = tagUserIds.stream()
                .filter(t -> previousTagUserList.stream().noneMatch(p -> p.getTagUserId().equals(t)))
                .collect(Collectors.toList());

        portPost.saveAllTagUser(addTagUserList, post.getPostId());
        portPost.deleteAllTagUser(removeTagUserList);
        return portPost.getListTagUserByPostId(post.getPostId(), new ArrayList<>());
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

    private List<TagUser> getTagUsers(List<Long> tagUserIds, Post post) {
        return tagUserIds.stream()
                .map(u -> createTagUser(u, post))
                .collect(Collectors.toList());
    }

    private List<ImagePost> handleImagePosts(List<String> publicIds, Post post) {
        List<ImagePost> imagePostEntities = new ArrayList<>();
        List<Long> imagePostSort = new ArrayList<>();
        List<String> keyLoadings = new ArrayList<>();
        String tail = ImagePostInput.POST_TAIL + authPort.getUserAuthOrDefaultVirtual().getUserEmail();

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

        imagePostPort.saveImageSequence(new ImageSequence(post.getPostId().toString(), imagePostSort));
        return imagePostPort.saveAllImagePost(imagePostEntities);
    }

    private List<ImagePost> updateImagePosts(PostRequest postRequest, Post post) {
        List<Long> imageIds = postRequest.getImageIds();
        List<String> publicIds = postRequest.getPublicIds();
        String tail = ImagePostInput.POST_TAIL + "_" + authPort.getUserAuth().getUserEmail();

        long cntZero = imageIds.stream().filter(id -> id == 0).count();
        if (cntZero != publicIds.size()) {
            throw new CustomException("Not enough images", HttpStatus.BAD_REQUEST);
        }

        int cnt = 0;
        for (int i = 0; i < imageIds.size(); i++) {
            if (imageIds.get(i) == 0) {
                String fullKey = publicIds.get(cnt) + tail;
                if (!redisImageTemplatePort.existsByKey(fullKey)) {
                    cnt++;
                    continue;
                }
                ImagePost imagePost = new ImagePost(
                        redisImageTemplatePort.findByKey(fullKey),
                        new Date(),
                        post.getPostId());
                ImagePost newImagePost = imagePostPort.saveImagePost(imagePost);
                imageIds.set(i, newImagePost.getImagePostId());
                redisImageTemplatePort.deleteByKey(publicIds.get(cnt) + ImagePostInput.POST_TAIL + authPort.getUserAuthOrDefaultVirtual().getUserEmail());
                cnt++;
            }
            if (cnt > postRequest.getImageIds().size()) {
                break;
            }
        }
        imagePostPort.saveImageSequence(new ImageSequence(post.getPostId().toString(), imageIds));

        // delete image in database
        List<ImagePost> currentImageIdsInDb = imagePostPort.findAllImagePost(post.getPostId());
        List<ImagePost> imagesNotInList = currentImageIdsInDb.stream()
                .filter(imagePost -> !imageIds.contains(imagePost.getImagePostId()))
                .toList();
        imagePostInput.deleteImagePost(imagesNotInList);

        return sortImagePosts(post.getPostId(), portPost.getListImageByPostId(post.getPostId()));
    }

    private List<ImagePost> sortImagePosts(Long postId, List<ImagePost> imagePosts) {
        Optional<ImageSequence> imageSequenceOpt = imagePostPort.findImageSequenceByPostId(postId);

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

    private PostResponse mapPostToResponse(Post p) {
        List<ImagePost> imagePostList = portPost.getListImageByPostId(p.getPostId());
        List<Long> blockIds = friendShipPort.getListBlockBoth(p.getUserId());
        List<TagUser> tagUserList = portPost.getListTagUserByPostId(p.getPostId(), blockIds);
        return postMapper.postToPostResponse(p, imagePostList, tagUserList);
    }

}