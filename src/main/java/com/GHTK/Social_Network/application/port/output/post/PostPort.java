package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.TagUser;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.entity.post.ImagePostEntity;

import java.util.List;

public interface PostPort {
  Post savePost(Post postEntity);

  Post findPostById(Long id);

  User findUserByPost(Post postEntity);

  List<Post> findAllPostByUser(User user);

  Post findPostByPostId(Long postId);

  User findUserById(Long id);

  Boolean deletePostById(Long id);

  List<Post> findAllPostTagMe(User user);

  Post findPostByImagePost(ImagePostEntity imagePost);

  TagUser saveTagUser(TagUser tagUserEntity);
}
