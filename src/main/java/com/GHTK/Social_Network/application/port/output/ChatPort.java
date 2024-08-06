package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.model.user.User;

import java.util.List;

public interface ChatPort {
  List<Long> getUserIdsFromChannel(Long channelId);

  boolean isUserInGroup(User currentUser);
}
