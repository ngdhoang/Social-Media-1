package com.GHTK.Social_Network.application.service.chat;

import com.GHTK.Social_Network.application.port.input.ChatPortInput;
import com.GHTK.Social_Network.application.port.output.ChatPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.WebSocketPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.websocket.WebsocketContextHolder;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ChatMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.MessageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService implements ChatPortInput {
  private final FriendShipPort friendShipPort;
  private final WebSocketPort webSocketPort;
  private final ChatPort chatPort;
  private final ChatMapper chatMapper;

  @Override
  public void handleIncomingMessage(MessageDto message) {
    UserBasicDto currentUser = WebsocketContextHolder.getContext().getUser();
    List<Long> receiveIds = new ArrayList<>();

    if (message.getGroupType().equals(EGroupType.PERSONAL)) { // if peer to peer
      // check block
      if (friendShipPort.isBlock(currentUser.getUserId(), message.getGroupId())) {
        throw new CustomException("You not permission", HttpStatus.FORBIDDEN);
      }

      // check user delete

      receiveIds.add(message.getGroupId()); // add user receive
    } else { // if channel type is group or public

      // check user delete
      if (chatPort.isUserInGroup(currentUser.getUserId())) { // check user in group
        receiveIds = chatPort.getUserIdsFromChannel(message.getGroupId());
      }
    } // no check block: group, public
    for (Long receiveId : receiveIds) {
      webSocketPort.SendAndSaveChatMessage(chatMapper.messageDtoToMessage(message), currentUser.getUserId(), receiveId);
    }
  }


}