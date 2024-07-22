package com.GHTK.Social_Network.domain.repository;

import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.TagUser;
import com.GHTK.Social_Network.domain.model.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository {
  List<Post> findAllByUser(User user);

  Post findByTagUsers(TagUser tagUser);

  Post findByImagePosts(ImagePost imagePost);
}
