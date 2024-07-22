package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.post.ReactionPostPort;
import com.GHTK.Social_Network.infrastructure.entity.post.ReactionPost;
import com.GHTK.Social_Network.infrastructure.repository.ReactionPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactionPostPortImpl implements ReactionPostPort {
  private final ReactionPostRepository reactionPostRepository;

  @Override
  public ReactionPost findByPostIdAndUserID(Long postId, Long userId) {
    return reactionPostRepository.findByPostIdAndUserID(postId, userId);
  }

  @Override
  public ReactionPost saveReaction(ReactionPost reactionPost) {
    return reactionPostRepository.save(reactionPost);
  }

  @Override
  public void deleteReaction(ReactionPost reactionPost) {
    reactionPostRepository.delete(reactionPost);
  }

  @Override
  public List<ReactionPost> findByPostId(Long postId) {
    return reactionPostRepository.findByPostId(postId);
  }
}
