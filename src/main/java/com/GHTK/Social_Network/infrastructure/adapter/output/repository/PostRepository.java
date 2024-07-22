package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ImagePost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.Post;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.TagUser;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  Optional<Post> findByPostIdAndUser(Long postId, UserEntity userEntity);

  List<Post> findAllByUser(UserEntity userEntity);

  Post findByTagUsers(TagUser tagUser);

  Optional<Post> findByImagePosts(ImagePost imagePost);
}
