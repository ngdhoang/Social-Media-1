package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.chat;

import com.GHTK.Social_Network.application.port.output.ChatPort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatAdapter implements ChatPort {
  @Override
  public List<Long> getUserIdsFromChannel(Long channelId) {
    return new ArrayList<>(1);
  }

  @Override
  public boolean isUserInGroup(Long userId) {
    return false;
  }
}
