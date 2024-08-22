package com.GHTK.Social_Network.application.port.output.chat;

import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;

import java.util.List;

public interface WebsocketClientPort {
  void sendUserAndNotSave(Message message, String destination);

  void sendUserAndSave(EGroupType groupType, Message message, String destination);

  void sendRelyUserAndSave(EGroupType groupType, Message message, Message messageQuote, String destination);

  void sendUserError(String error, Long userReceiveId);

  void sendListUserAndSave(Message messages, List<Long> receiverIds);

  void sendReplyListUserAndSave(Message messages, Message messageQuote, List<Long> receiverIds);

  void sendListUserAndNotSave(Message messages, List<Long> receiverIds);

  void sendListUserError(String error, List<Long> userReceiveIds);

  Message createNotificationMessage(Long currentUserId, String content);

  UserBasicDto getUserAuth();
}
