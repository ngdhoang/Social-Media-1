package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.post;

import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ImagePost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.Post;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.TagUser;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.PostRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.TagUserRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostPortImpl implements PostPort {
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
  public UserEntity findUserByPost(Post post) {
    return userRepository.findByPosts(post).orElse(null);
  }

  @Override
  public List<Post> findAllPostByUser(UserEntity userEntity) {
    return postRepository.findAllByUser(userEntity);
  }

  @Override
  public Post findPostByPostId(Long postId) {
    return postRepository.findById(postId).orElse(null);
  }

  @Override
  public UserEntity findFriendById(Long id) {
    return null;
  }

  @Override
  public UserEntity findUserById(Long id) {
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
  public List<Post> findAllPostTagMe(UserEntity userEntity) {
    List<TagUser> tagUserList = tagUserRepository.findAllByUser(userEntity);
    List<Post> postList = new ArrayList<>();
    tagUserList.forEach(tagUser -> {
      postList.add(postRepository.findByTagUsers(tagUser));
    });
    return postList;
  }

  @Override
  public Post findPostByImagePost(ImagePost imagePost) {
    return postRepository.findByImagePosts(imagePost).orElse(null);
  }

  @Override
  public TagUser saveTagUser(TagUser tagUser) {
    return tagUserRepository.save(tagUser);
  }
}
