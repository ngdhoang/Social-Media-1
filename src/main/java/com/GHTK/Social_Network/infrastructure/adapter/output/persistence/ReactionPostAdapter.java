package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.post.ReactionPostPort;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ReactionPostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ReactionPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactionPostAdapter implements ReactionPostPort {
  private final ReactionPostRepository reactionPostRepository;

  @Override
  public ReactionPostEntity findByPostIdAndUserID(Long postId, Long userId) {
    return reactionPostRepository.findByPostIdAndUserID(postId, userId);
  }

  @Override
  public ReactionPostEntity saveReaction(ReactionPostEntity reactionPostEntity) {
    return reactionPostRepository.save(reactionPostEntity);
  }

  @Override
  public void deleteReaction(ReactionPostEntity reactionPostEntity) {
    reactionPostRepository.delete(reactionPostEntity);
  }

  @Override
  public List<ReactionPostEntity> findByPostId(Long postId) {
    return reactionPostRepository.findByPostId(postId);
  }
}
