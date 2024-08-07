package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.infrastructure.payload.responses.ChatMessageResponse;

public interface WebsocketPort {
  void sendAndNotSave(ChatMessageResponse message, Long receiverId);

  void sendAndSave(EGroupType groupType, Message message, Long sendId, Long receiverId);

  void sendErrorForMe(String error, Long userReceiveId);
}
