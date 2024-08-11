package com.GHTK.Social_Network.application.port.output.chat;

import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.ChatMessageResponse;

import java.util.List;

public interface WebsocketClientPort {
  void sendUserAndNotSave(Message message, String destination);

  void sendUserAndSave(EGroupType groupType, Message message, String destination);

  void sendUserError(String error, Long userReceiveId);

  void sendListUserAndSave(Message messages, List<Long> receiverIds);

  void sendListUserAndNotSave(Message messages, List<Long> receiverIds);

  void sendListUserError(String error, List<Long> userReceiveIds);

  UserBasicDto getUserAuth();
}
