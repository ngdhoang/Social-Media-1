package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.post;

import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.domain.entity.post.Post;
import com.GHTK.Social_Network.domain.entity.post.TagUser;
import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.post.PostRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.TagUserRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortPostImpl implements PostPort {
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final TagUserRepository tagUserRepository;

  @Override
  public Post savePost(Post post) {
    return postRepository.save(post);
  }

  @Override
  public Post findPostById(Long id) {
    return postRepository.findById(id).orElse(null);
  }

  @Override
  public User findUserByPost(Post post) {
    return userRepository.findByPosts(post).orElse(null);
  }

  @Override
  public List<Post> findAllPostById(User user) {
    List<TagUser> tagUserList = tagUserRepository.findAllByUser(user);
    List<Post> postList = new ArrayList<>();
    tagUserList.forEach(tagUser -> {
      postList.add(postRepository.findByTagUsers(tagUser));
    });
    return postList;
  }

  @Override
  public Post findPostByPostIdAndUser(Long postId, User user) {
    return postRepository.findByPostIdAndUser(postId, user).orElse(null);
  }

  @Override
  public User findFriendById(Long id) {
    return null;
  }

  @Override
  public User findUserById(Long id) {
    return userRepository.findById(id).orElse(null);
  }

  @Override
  public Boolean deletePostById(Long id) {
    try {
      postRepository.deleteById(id);
      return true;
    } catch (EmptyResultDataAccessException e) {
      return false;
    }
  }

  @Override
  public List<Post> findAllPostTagMe(User user) {
    return List.of();
  }
}
