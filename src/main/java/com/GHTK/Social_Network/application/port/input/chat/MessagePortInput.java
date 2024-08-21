package com.GHTK.Social_Network.application.port.input.chat;

import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.ReactionRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.chat.ChatMessageReplyResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.chat.ChatMessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.ReactionChatResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.chat.ListChatMessageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MessagePortInput {
  ChatMessageResponse deleteMessage(String messageId);

  ChatMessageResponse recallMessage(String messageId);

  ChatMessageResponse reactionMessage(String messageId, ReactionRequest reactionRequest);

  MessageResponse sendListImage(List<MultipartFile> images, String groupId);

  ReactionChatResponse getReactionMessage(String messageId, String status);

  ListChatMessageResponse getMessages(String groupId, PaginationRequest paginationRequest);

  MessageResponse readMessages(String msgId);

  MessageResponse typingMessages(String groupId);
}
