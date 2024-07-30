package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.post;

import com.GHTK.Social_Network.application.port.input.post.ImagePostInput;
import com.GHTK.Social_Network.application.port.output.CloudPort;
import com.GHTK.Social_Network.application.port.output.post.ImagePostPort;
import com.GHTK.Social_Network.application.port.output.post.RedisImageTemplatePort;
import com.GHTK.Social_Network.domain.collection.ImageSequence;
import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.ImageSequenceCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ImagePostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ImagePostRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ImageSequenceRepository;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.PostRepository;
import com.GHTK.Social_Network.infrastructure.mapper.ImagePostMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImagePostAdapter implements ImagePostPort {
  private final ImagePostRepository imagePostRepository;
  private final RedisImageTemplatePort redisImageTemplatePort;
  private final ImageSequenceRepository imageSequenceRepository;
  private final PostRepository postRepository;
  private final ImagePostMapperETD imagePostMapperETD;
  private final CloudPort cloudPort;

  @Override
  public ImagePost findImageById(Long id) {
    return imagePostMapperETD.toDomain(imagePostRepository.findById(id).orElse(null));
  }

  @Override
  public void deleteImageById(Long id) {
    imagePostRepository.deleteById(id);
  }

  @Async
  @Override
  public void deleteAllImageRedisByTail(String tail) {
    Set<String> keys = redisImageTemplatePort.findAllByKeys("*" + tail);

    if (!keys.isEmpty()) {
      keys.forEach(k -> {
        String value = redisImageTemplatePort.findByKey(k);
        if (!value.equals(ImagePostInput.VALUE_LOADING))
          cloudPort.deletePictureByUrl(redisImageTemplatePort.findByKey(k)); // Delete image in cloud
        redisImageTemplatePort.deleteByKey(k);
      });
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
  public List<ImagePost> findAllImagePost(Long postId) {
    PostEntity p = postRepository.findById(postId).orElse(null);
    if (p == null) {
      return List.of();
    }

    List<ImagePostEntity> imagePostEntities = p.getImagePostEntities();
    if (imagePostEntities == null) {
      return List.of();
    }

    return imagePostEntities.stream()
            .map(imagePostMapperETD::toDomain)
            .filter(Objects::nonNull)
            .toList();
  }

  @Override
  public ImageSequence saveImageSequence(ImageSequence imageSequence) {
    ImageSequenceCollection newImageSequenceCollection = ImageSequenceCollection.builder()
            .postId(imageSequence.getPostId())
            .listImageSort(imageSequence.getListImageSort())
            .build();
    return imagePostMapperETD.toDomain(imageSequenceRepository.save(newImageSequenceCollection));
  }

  @Override
  public Optional<ImageSequence> findImageSequenceByPostId(Long postId) {
    try {
      return imageSequenceRepository.findByPostId(postId.toString())
              .map(imageSequenceCollection -> new ImageSequence(
                      imageSequenceCollection.getPostId(),
                      imageSequenceCollection.getListImageSort()
              ));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
