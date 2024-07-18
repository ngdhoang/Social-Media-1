package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.entity.post.Post;
import com.GHTK.Social_Network.domain.entity.user.User;

import java.util.List;

public interface PostPort {
  Post savePost(Post post);

  Post findPostById(Long id);

  User findUserByPost(Post post);

  List<Post> findAllPostById(User user);

  Post findPostByPostIdAndUser(Long postId, User user);

  User findFriendById(Long id);

  User findUserById(Long id);

  Boolean deletePostById(Long id);

  List<Post> findAllPostTagMe(User user);
}
