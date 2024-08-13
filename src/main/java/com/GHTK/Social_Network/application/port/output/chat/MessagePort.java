package com.GHTK.Social_Network.application.port.output.chat;

import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.domain.collection.chat.ReactionMessages;
import com.GHTK.Social_Network.domain.model.post.EReactionType;

public interface MessagePort {
  Message getMessageById(String messageId);

  Message saveMessage(Message newMessage);

  ReactionMessages getReactionByUserIdAndMsgId(String msgId, Long userId);

  void saveOrChangeReactionMessage(String msgId, Long userId, EReactionType reactionType);

  void deleteReactionMessage(String msgId, Long userId);
}
