package com.GHTK.Social_Network.application.port.output.chat;

import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface MessagePort {
  Message getMessageById(String messageId);

  Message saveMessage(Message newMessage);

  Message getLastMessageByGroupId(String groupId);

  void saveOrChangeReactionMessage(String msgId, Long userId, EReactionType reactionType);

  List<Pair<Message, Message>> getMessagesByGroupId(String groupId, PaginationRequest paginationRequest);
}
