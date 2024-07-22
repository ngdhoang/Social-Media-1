package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.model.User;
import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.TagUser;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;

import java.util.List;

public interface PostPort {
  Post savePost(Post post);

  Post findPostById(Long id);

  User findUserByPost(Post post);

  List<Post> findAllPostByUser(User user);

  Post findPostByPostId(Long postId);

  User findFriendById(Long id);

  User findUserById(Long id);

  Boolean deletePostById(Long id);

  List<Post> findAllPostTagMe(User user);

  Post findPostByImagePost(ImagePost imagePost);

  TagUser saveTagUser(TagUser tagUser);

  TagUser saveAllTagUser(List<TagUser> tagUser);
}
