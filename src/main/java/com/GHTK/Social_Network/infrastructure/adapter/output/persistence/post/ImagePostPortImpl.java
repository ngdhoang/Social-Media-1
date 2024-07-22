package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.post;

import com.GHTK.Social_Network.application.port.output.post.ImagePostPort;
import com.GHTK.Social_Network.domain.model.collection.ImageSequenceDomain;
import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.ImageSequence;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ImagePostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ImagePostRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ImageSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
  public void deleteImageRedisByPublicId(List<String> publicIds, String tail) {
//    publicIds.forEach(publicId -> {
//      imageRedisTemplate.delete(publicId);
//      imageRedisTemplate.delete(publicId);
//    });
    Set<String> keys = imageRedisTemplate.keys("*" + tail);

    if (keys != null) {
      keys.forEach(imageRedisTemplate::delete);
    }
  }

  @Override
  public ImagePost saveImagePost(ImagePost imagePost) {
    return null;
  }

  @Override
  public ImageSequence saveImageSequence(ImageSequenceDomain imageSequence) {
    return null;
  }

  @Override
  public ImagePostEntity saveImagePost(ImagePostEntity imagePostEntity) {
    return imagePostRepository.save(imagePostEntity);
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
