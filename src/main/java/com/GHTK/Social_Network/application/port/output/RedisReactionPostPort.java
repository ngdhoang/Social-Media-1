package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.application.port.output.template.RedisTemplatePort;
import com.GHTK.Social_Network.infrastructure.payload.requests.GetReactionPostRequest;

import java.util.List;

public interface RedisReactionPostPort extends RedisTemplatePort<String, ReactionPostRedisDto>{
  List<ReactionPostRedisDto> getListsReactionPostByPostId(GetReactionPostRequest getReactionPostRequest, Long postId);
}
