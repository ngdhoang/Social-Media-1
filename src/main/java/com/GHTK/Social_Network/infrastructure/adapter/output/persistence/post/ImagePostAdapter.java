package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.post;

import com.GHTK.Social_Network.application.port.output.post.ImagePostPort;
import com.GHTK.Social_Network.domain.model.collection.ImageSequenceDomain;
import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.ImageSequence;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ImagePostRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ImageSequenceRepository;
import com.GHTK.Social_Network.infrastructure.mapper.ImagePostMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImagePostAdapter implements ImagePostPort {
  private final ImagePostRepository imagePostRepository;

  private final RedisTemplate<String, String> imageRedisTemplate;

  private final ImageSequenceRepository imageSequenceRepository;

  private final ImagePostMapperETD imagePostMapperETD;

  @Override
  public ImagePost findImageById(Long id) {
    return imagePostMapperETD.toDomain(imagePostRepository.findById(id).orElse(null));
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
    return imagePostMapperETD.toDomain(imagePostRepository.save(imagePostMapperETD.toEntity(imagePost)));
  }

  @Override
  public List<ImagePost> saveAllImagePost(List<ImagePost> imagePosts) {
    return imagePosts.stream()
            .map(this::saveImagePost)
            .toList();
  }

  @Override
  public ImageSequenceDomain saveImageSequence(ImageSequenceDomain imageSequence) {
    ImageSequence newImageSequence = ImageSequence.builder()
            .postId(imageSequence.getPostId())
            .listImageSort(imageSequence.getListImageSort())
            .build();
    return imagePostMapperETD.toDomain(imageSequenceRepository.save(newImageSequence));
  }

  @Override
  public ImageSequenceDomain findImageSequenceByPostId(Long postId) {
    ImageSequence imageSequence = imageSequenceRepository.findByPostId(postId);
    ImageSequenceDomain imageSequenceDomain = new ImageSequenceDomain(
            imageSequence.getPostId(),
            imageSequence.getListImageSort()
    );
    return imageSequenceDomain;
  }
}
