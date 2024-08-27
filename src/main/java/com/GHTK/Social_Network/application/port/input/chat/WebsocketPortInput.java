package com.GHTK.Social_Network.application.port.input.chat;

import com.GHTK.Social_Network.infrastructure.payload.dto.chat.MessageDto;

public interface WebsocketPortInput {
  void handleIncomingMessage(MessageDto message);
}
