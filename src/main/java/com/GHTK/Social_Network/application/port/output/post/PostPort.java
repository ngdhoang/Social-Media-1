package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.TagUser;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.GetPostRequest;

import java.util.List;

public interface PostPort {
  enum TAKE_POST_STATUS {
    PUBLIC,
    FRIEND,
    PRIVATE,
    ALL
  }

  List<Post> getListPostSuggest(Long userId, GetPostRequest getPostRequest);

  Post savePost(Post post);

  Post findPostById(Long id);

  List<Post> findPostsByUserIdAndFriendStatus(Long userId, TAKE_POST_STATUS status, List<Long> blockIds, GetPostRequest getPostRequest);

  Post findPostByPostId(Long postId);

  Boolean deletePostById(Long id);

  List<Post> findPostsTagMe(Long currentUser, List<Long> blockIds, GetPostRequest getPostRequest);

  List<Post> findPostsWithUserInteractions(Long userId);

  TagUser saveTagUser(TagUser tagUser);

  List<TagUser> saveAllTagUser(List<Long> tagUsers, Long postId);

  List<TagUser> saveAllTagUser(List<TagUser> tagUsers);

  void deleteAllTagUser(List<TagUser> tagUserIds);

  List<ImagePost> getListImageByPostId(Long postId);

  List<TagUser> getListTagUserByPostId(Long postId, List<Long> blockIds);

  void decrementReactionQuantity(Long postId);

  void incrementReactionQuantity(Long postId);

  void decrementCommentQuantity(Long postId, Long numberOfComments);

  void incrementCommentQuantity(Long postId);
}
