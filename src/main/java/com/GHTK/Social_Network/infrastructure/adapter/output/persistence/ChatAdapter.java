package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.ChatPort;
import com.GHTK.Social_Network.domain.model.user.User;
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
  public boolean isUserInGroup(User currentUser) {
    return false;
  }
}
