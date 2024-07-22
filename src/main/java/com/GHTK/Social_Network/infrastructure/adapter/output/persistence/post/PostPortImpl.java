package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.post;

import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.MapperEntity.PostMapper;
import com.GHTK.Social_Network.infrastructure.MapperEntity.UserMapper;
import com.GHTK.Social_Network.infrastructure.entity.post.ImagePostEntity;
import com.GHTK.Social_Network.infrastructure.repository.PostEntityRepository;
import com.GHTK.Social_Network.infrastructure.repository.TagUserEntityRepository;
import com.GHTK.Social_Network.infrastructure.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostPortImpl implements PostPort {
  private final UserEntityRepository UserEntityRepository;
  private final PostEntityRepository postEntityRepository;
  private final TagUserEntityRepository tagUserEntityRepository;
  private final PostMapper postMapper;
  private final UserMapper userMapper;

  @Override
  public Post savePost(Post post) {
    return postMapper.toDomain(postEntityRepository.save(postMapper.toEntity(post)));
  }

  @Override
  public Post findPostById(Long id) {
    return postMapper.toDomain(postEntityRepository.findById(id).orElse(null));
  }

  @Override
  public User findUserByPost(Post Post) {
    return userMapper.toDomain(.findByPostI(Post).orElse(null));
  }

  @Override
  public List<Post> findAllPostByUser(User User) {
    return postEntityRepository.findAllByUser(User);
  }

  @Override
  public Post findPostByPostId(Long postId) {
    return postEntityRepository.findById(postId).orElse(null);
  }

  @Override
  public User findUserById(Long id) {
    return UserRepository.findById(id).orElse(null);
  }

  @Override
  public Boolean deletePostById(Long id) {
    try {
      postEntityRepository.deleteById(id);
      return true;
    } catch (EmptyResultDataAccessException e) {
      return false;
    }
  }

  @Override
  public List<Post> findAllPostTagMe(User User) {
    List<TagUser> tagUserList = tagUserEntityRepository.findAllByUser(User);
    List<Post> PostList = new ArrayList<>();
    tagUserList.forEach(tagUser -> {
      PostList.add(postEntityRepository.findByTagUsers(tagUser));
    });
    return PostList;
  }

  @Override
  public Post findPostByImagePost(ImagePostEntity imagePost) {
    return postEntityRepository.findByImagePosts(imagePost).orElse(null);
  }

  @Override
  public TagUser saveTagUser(TagUser tagUser) {
    return tagUserEntityRepository.save(tagUser);
  }
}
