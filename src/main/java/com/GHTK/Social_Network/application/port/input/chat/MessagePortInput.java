package com.GHTK.Social_Network.application.port.input.chat;

import com.GHTK.Social_Network.infrastructure.payload.responses.ChatMessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MessagePortInput {
  ChatMessageResponse deleteMessage(String messageId);

  ChatMessageResponse recallMessage(String messageId);

  ChatMessageResponse reactionMessage(String messageId);

  MessageResponse sendListImage(List<MultipartFile> images, String groupId);
}
