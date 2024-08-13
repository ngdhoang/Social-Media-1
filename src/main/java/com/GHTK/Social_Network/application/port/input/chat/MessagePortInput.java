package com.GHTK.Social_Network.application.port.input.chat;

import com.GHTK.Social_Network.infrastructure.payload.responses.ChatMessageResponse;

public interface MessagePortInput {
  ChatMessageResponse deleteMassage(String messageId);
}
