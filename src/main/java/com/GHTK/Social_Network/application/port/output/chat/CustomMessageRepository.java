package com.GHTK.Social_Network.application.port.output.chat;

import com.GHTK.Social_Network.domain.model.post.EReactionType;

public interface CustomMessageRepository {
  void updateOrAddReactionMessage(String msgId, Long userId, EReactionType reactionType);

  void deleteReactionMessageCustom(String msgId, Long userId);
}
