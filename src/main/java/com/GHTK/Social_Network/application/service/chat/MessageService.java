package com.GHTK.Social_Network.application.service.chat;

import com.GHTK.Social_Network.application.port.input.chat.MessagePortInput;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.chat.GroupPort;
import com.GHTK.Social_Network.application.port.output.chat.MessagePort;
import com.GHTK.Social_Network.application.port.output.chat.WebsocketClientPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.collection.chat.*;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ChatMapper;
import com.GHTK.Social_Network.infrastructure.payload.responses.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService implements MessagePortInput {
  private final AuthPort authPort;
  private final GroupPort groupPort;
  private final MessagePort messagePort;
  private final WebsocketClientPort websocketClientPort;

  private final ChatMapper chatMapper;

  @Override
  public ChatMessageResponse deleteMassage(String messageId) {
    User currentUser = authPort.getUserAuth();
    Message message = messagePort.getMessageById(messageId);

    if (message == null) {
      throw new CustomException("Message not found", HttpStatus.NOT_FOUND);
    }

    Group group = groupPort.getGroup(message.getGroupId());
    if (!message.getUserAuthId().equals(authPort.getUserAuth().getUserId()) ||
            !group.getMembers().stream().map(Member::getUserId).toList().contains(currentUser.getUserId())) {
      throw new CustomException("You are not allowed to delete this message", HttpStatus.FORBIDDEN);
    }

    message.setMsgType(EMessageType.DELETE); // Delete for me

    websocketClientPort.sendListUserAndNotSave(message, group.getMembers().stream().map(Member::getUserId).toList());
    return chatMapper.messageToMessageResponse(message, EGroupType.GROUP);
  }
}
