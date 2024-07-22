package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ImagePost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.Post;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.TagUser;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;

import java.util.List;

public interface PostPort {
  Post savePost(Post post);

  Post findPostById(Long id);

  UserEntity findUserByPost(Post post);

  List<Post> findAllPostByUser(UserEntity userEntity);

  Post findPostByPostId(Long postId);

  UserEntity findFriendById(Long id);

  UserEntity findUserById(Long id);

  Boolean deletePostById(Long id);

  List<Post> findAllPostTagMe(UserEntity userEntity);

  Post findPostByImagePost(ImagePost imagePost);

  TagUser saveTagUser(TagUser tagUser);
}
