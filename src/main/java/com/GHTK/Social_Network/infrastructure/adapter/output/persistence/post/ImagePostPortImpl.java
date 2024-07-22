package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.post;

import com.GHTK.Social_Network.application.port.output.post.ImagePostPort;
import com.GHTK.Social_Network.infrastructure.entity.collection.ImageSequence;
import com.GHTK.Social_Network.infrastructure.entity.post.ImagePostEntity;
import com.GHTK.Social_Network.infrastructure.repository.ImagePostRepository;
import com.GHTK.Social_Network.infrastructure.repository.ImageSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImagePostPortImpl implements ImagePostPort {
  private final ImagePostRepository imagePostRepository;

  private final RedisTemplate<String, String> imageRedisTemplate;

  private final ImageSequenceRepository imageSequenceRepository;

  @Override
  public ImagePostEntity findImageById(Long id) {
    return imagePostRepository.findById(id).orElse(null);
  }

  @Override
  public void deleteImageById(Long id) {
    imagePostRepository.deleteById(id);
  }

  @Override
  public void deleteImageRedisByPublicId(List<String> publicId) {
    publicId.forEach(imageRedisTemplate::delete);
  }

  @Override
  public ImagePostEntity saveImagePost(ImagePostEntity imagePost) {
    return imagePostRepository.save(imagePost);
  }

  @Override
  public ImageSequence saveImageSequence(ImageSequence imageSequence) {
    return imageSequenceRepository.save(imageSequence);
  }

  @Override
  public ImageSequence findImageSequenceByPostId(Long postId) {
    return imageSequenceRepository.findByPostId(postId).orElse(null);
  }
}
