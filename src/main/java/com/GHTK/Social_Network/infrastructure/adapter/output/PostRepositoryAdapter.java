package com.GHTK.Social_Network.infrastructure.adapter.output;

import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.post.TagUser;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.domain.repository.PostRepository;
import com.GHTK.Social_Network.infrastructure.repository.PostEntityRepository;
import com.GHTK.Social_Network.infrastructure.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostRepositoryAdapter implements PostRepository {
  private final PostEntityRepository postEntityRepository;
  private final UserEntityRepository userEntityRepository;

  @Override
  public List<Post> findAllByUser(User user) {

    return postEntityRepository.findAllByUserEntity();
  }

  @Override
  public Post findByTagUsers(TagUser tagUser) {
    return null;
  }

  @Override
  public Optional<Post> findByImagePosts(ImagePost imagePost) {
    return Optional.empty();
  }
}
