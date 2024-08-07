package com.GHTK.Social_Network.application.service.chat;

import com.GHTK.Social_Network.application.port.input.WebsocketPortInput;
import com.GHTK.Social_Network.application.port.output.ChatPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.WebsocketPort;
import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.infrastructure.adapter.input.websocket.WebsocketContextHolder;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ChatMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.MessageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebsocketService implements WebsocketPortInput {
  private final FriendShipPort friendShipPort;
  private final WebsocketPort webSocketPort;
  private final ChatPort chatPort;

  private final ChatMapper chatMapper;

  @Override
  public void handleIncomingMessage(MessageDto message) {
    UserBasicDto currentUser = WebsocketContextHolder.getContext().getUser();

    Group currentGroup = chatPort.getGroup(message.getGroupId());
    boolean isGroup = message.getGroupType().equals(EGroupType.GROUP);
    boolean isGroupExists = currentGroup != null;

    if (isGroup && !isGroupExists) {
      this.sendError("Group not exists", currentUser.getUserId());
      return;
    }

    if (isGroup) {
      sendToGroup(message, currentUser.getUserId(), currentGroup.getGroupId());
    } else {
      Long userReceiveId = currentUser.getUserId().equals(currentGroup.getMembers().get(0).getUserId())
              ? currentGroup.getMembers().get(1).getUserId()
              : currentGroup.getMembers().get(0).getUserId();
      validateFriendShip(currentUser.getUserId(), userReceiveId, isGroupExists);
      sendToUser(message, currentUser.getUserId(), userReceiveId);
    }
  }

  private void validateFriendShip(Long currentId, Long userId, boolean isGroupExists) {
    if (friendShipPort.isDeleteUser(userId)) { // check user exist
      sendError("User not exists", currentId);
    }

    if (isGroupExists && friendShipPort.isBlock(currentId, userId)) { // check block
      sendError("User is blocked", currentId);
    }

    if (!isGroupExists && !friendShipPort.isFriend(currentId, userId)) { // check friend
      sendError("User is not friend", currentId);
    }
  }

  private void sendToUser(MessageDto msg, Long userIdSend, Long userReceiveId) {
    webSocketPort.sendAndSave(msg.getGroupType(), chatMapper.messageDtoToMessage(msg), userIdSend, userReceiveId);
  }

  private void sendToGroup(MessageDto msg, Long userIdSend, String groupId) {
    Group group = chatPort.getGroup(groupId);
    group.getMembers().forEach(member ->
            sendToUser(msg, userIdSend, member.getUserId())
    );
  }

  private void sendError(String error, Long userReceiveId) {
    webSocketPort.sendErrorForMe(error, userReceiveId);
  }
}
