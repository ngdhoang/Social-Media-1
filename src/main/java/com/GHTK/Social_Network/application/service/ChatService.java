package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.ChatPortInput;
import com.GHTK.Social_Network.application.port.output.ChatPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.WebSocketPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.EChatMessageType;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.MessageDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService implements ChatPortInput {
  private final FriendShipPort friendShipPort;
  private final AuthPort authPort;
  private final WebSocketPort webSocketPort;
  private final ChatPort chatPort;

  @Override
  public void handleIncomingMessage(MessageDto message) {
    User currentUser = authPort.getUserAuth();
    List<Long> receiveIds = new ArrayList<>();

    if (message.getGroupType().equals(EChatMessageType.USER)) { // if peer to peer
      // check block
      if (friendShipPort.isBlock(currentUser.getUserId(), message.getGroupId())){
        throw new CustomException("You not permission", HttpStatus.FORBIDDEN);
      }

      receiveIds.add(message.getGroupId()); // add user receive
    } else { // if channel type is group or public
      if (chatPort.isUserInGroup(currentUser)){ // check user in group
        receiveIds = chatPort.getUserIdsFromChannel(message.getGroupId());
      }
    } // no check block: group, public
    for (Long receiveId : receiveIds) {
      webSocketPort.SendAndSaveChatMessage(new ChatMessageResponse(
              currentUser.getUserId(),
              message
      ), receiveId);
    }
  }


}
