package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.post;

import com.GHTK.Social_Network.application.port.output.CloudPort;
import com.GHTK.Social_Network.application.port.output.post.ImagePostPort;
import com.GHTK.Social_Network.application.port.output.post.RedisImageTemplatePort;
import com.GHTK.Social_Network.domain.model.collection.ImageSequenceDomain;
import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.domain.model.post.Post;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.ImageSequence;
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
    System.out.println(tail);
    Set<String> keys = redisImageTemplatePort.findAllByKeys("*" + tail);

    if (keys != null) {
      keys.forEach(k -> {
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
    return Objects.requireNonNull(p).getImagePostEntities().stream().map(
            imagePostMapperETD::toDomain
    ).toList();
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
  public Optional<ImageSequenceDomain> findImageSequenceByPostId(Long postId) {
    try {
      return imageSequenceRepository.findByPostId(postId.toString())
              .map(imageSequence -> new ImageSequenceDomain(
                      imageSequence.getPostId(),
                      imageSequence.getListImageSort()
              ));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
