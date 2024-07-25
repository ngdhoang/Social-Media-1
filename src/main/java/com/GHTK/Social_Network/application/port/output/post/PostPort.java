package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.TagUser;

import java.util.List;

public interface PostPort {
  enum TAKE_POST_STATUS {
    PUBLIC,
    FRIEND,
    PRIVATE,
    ALL
  }

  Post savePost(Post post);

  Post findPostById(Long id);

  List<Post> findPostsByUserIdAndFriendStatus(Long userId, TAKE_POST_STATUS status);

  Post findPostByPostId(Long postId);

  Boolean deletePostById(Long id);

  List<Post> findAllPostTagMeNotBlockAndPrivate(Long currentUser);

  Post findPostByImagePostId(Long imagePostId);

  TagUser saveTagUser(TagUser tagUser);

  List<TagUser> saveAllTagUser(List<TagUser> tagUser);

  List<ImagePost> findAllImageByPostId(Long postId);

  List<TagUser> findAllTagUserByPostId(Long postId);
}
