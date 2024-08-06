package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.infrastructure.payload.responses.ChatMessageResponse;

public interface WebSocketPort {
  void SendAndSaveChatMessage(ChatMessageResponse chatMessage, Long receiverId);
}
