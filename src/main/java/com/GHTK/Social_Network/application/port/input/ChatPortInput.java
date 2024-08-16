package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.infrastructure.payload.dto.MessageDto;

public interface ChatPortInput {
  void handleIncomingMessage(MessageDto message);
}
