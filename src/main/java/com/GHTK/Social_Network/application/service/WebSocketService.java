package com.GHTK.Social_Network.application.service;

import com.GHTK.Social_Network.application.port.input.WebSocketPortInput;
import com.GHTK.Social_Network.application.port.output.ChatPort;
import com.GHTK.Social_Network.application.port.output.FriendShipPort;
import com.GHTK.Social_Network.application.port.output.WebSocketPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.EChatMessageType;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.ChatMessageDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebSocketService implements WebSocketPortInput {
  private final FriendShipPort friendShipPort;
  private final AuthPort authPort;
  private final WebSocketPort webSocketPort;
  private final ChatPort chatPort;

  @Override
  public void handleIncomingMessage(ChatMessageDto message) {
    User currentUser = authPort.getUserAuth();
    List<Long> receiveIds = new ArrayList<>();
    if (message.getChannelType().equals(EChatMessageType.USER)) {
      // check block
      if (friendShipPort.isBlock(currentUser.getUserId(), message.getChannelId())){
        throw new CustomException("You not permission", HttpStatus.FORBIDDEN);
      }
      // add user receive
      receiveIds.add(message.getChannelId());
    } else {
      // if channel type is group or public
      receiveIds = chatPort.getUserIdsFromChannel(message.getChannelId());
    }
    for (Long receiveId : receiveIds) {
      webSocketPort.SendAndSaveChatMessage(new ChatMessageResponse(
              currentUser.getUserId(),
              message
      ), receiveId);
    }
    // no check block: group, public
  }


}
