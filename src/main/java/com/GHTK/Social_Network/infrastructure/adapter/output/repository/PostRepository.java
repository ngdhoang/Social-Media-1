package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.domain.entity.post.Post;
import com.GHTK.Social_Network.domain.entity.post.TagUser;
import com.GHTK.Social_Network.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  Optional<Post> findByPostIdAndUser(Long postId, User user);

  List<Post> findAllByUser(User user);

  Post findByTagUsers(TagUser tagUser);
}
