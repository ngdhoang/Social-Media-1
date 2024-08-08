package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.collection.chat.Message;

public interface WebSocketPort {
  void SendAndSaveChatMessage(Message message, Long sendId, Long receiverId);
}
