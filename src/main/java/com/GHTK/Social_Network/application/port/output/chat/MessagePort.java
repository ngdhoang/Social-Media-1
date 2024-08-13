package com.GHTK.Social_Network.application.port.output.chat;

import com.GHTK.Social_Network.domain.collection.chat.Message;

public interface MessagePort {
  Message getMessageById(String messageId);

  Message saveMessage(Message newMessage);
}
