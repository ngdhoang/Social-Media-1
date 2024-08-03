package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.dto.ChatMessageDto;

public interface WebSocketPortInput {
  void handleIncomingMessage(ChatMessageDto message);
}
