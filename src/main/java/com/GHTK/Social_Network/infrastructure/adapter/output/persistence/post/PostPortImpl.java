package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.post;

import com.GHTK.Social_Network.application.port.output.post.PostPort;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ImagePostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.TagUserEntity;
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
  public PostEntity savePost(PostEntity postEntity) {
    return postRepository.save(postEntity);
  }

  @Override
  public PostEntity findPostById(Long id) {
    return postRepository.findById(id).orElse(null);
  }

  @Override
  public UserEntity findUserByPost(PostEntity postEntity) {
    return userRepository.findByPosts(postEntity).orElse(null);
  }

  @Override
  public List<PostEntity> findAllPostByUser(UserEntity userEntity) {
    return postRepository.findAllByUser(userEntity);
  }

  @Override
  public PostEntity findPostByPostId(Long postId) {
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
  public List<PostEntity> findAllPostTagMe(UserEntity userEntity) {
    List<TagUserEntity> tagUserEntityList = tagUserRepository.findAllByUser(userEntity);
    List<PostEntity> postEntityList = new ArrayList<>();
    tagUserEntityList.forEach(tagUser -> {
      postEntityList.add(postRepository.findByTagUsers(tagUser));
    });
    return postEntityList;
  }

  @Override
  public PostEntity findPostByImagePost(ImagePostEntity imagePostEntity) {
    return postRepository.findByImagePosts(imagePostEntity).orElse(null);
  }

  @Override
  public TagUserEntity saveTagUser(TagUserEntity tagUserEntity) {
    return tagUserRepository.save(tagUserEntity);
  }
}
