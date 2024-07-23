package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.post.RedisReactionPostPort;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ReactionPostRedisDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionPostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisReactionPostAdapter implements RedisReactionPostPort {

  public final RedisTemplate<String, ReactionPostRedisDto> reactionPostRedisTemplate;
  @Override
  public ReactionPostRedisDto findByKey(String key) {
    return reactionPostRedisTemplate.opsForValue().get(formatKey(key));
  }

  @Override
  public void createOrUpdate(String key, ReactionPostRedisDto value) {
    reactionPostRedisTemplate.opsForValue().set(formatKey(key), value);
  }

  @Override
  public void deleteByKey(String key) {
    reactionPostRedisTemplate.delete(formatKey(key));
  }

  @Override
  public String formatKey(String key) {
    return "reaction-post:" + key ;
  }

  @Override
  public Boolean existsByKey(String key) {
    return null;
  }

  @Override
  public List<ReactionPostRedisDto> getListsReactionPostByPostId(GetReactionPostRequest getReactionPostRequest, Long postId) {
    ReactionPostRedisDto reactionPostRedisDto = findByKey(String.valueOf(postId));


    return null;
  }

}
