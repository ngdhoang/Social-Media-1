package com.GHTK.Social_Network.application.port.input.chat;

import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;

public interface CallVideoPortInput {
  MessageResponse ring(String groupId);

  MessageResponse reject(String groupId);
}
