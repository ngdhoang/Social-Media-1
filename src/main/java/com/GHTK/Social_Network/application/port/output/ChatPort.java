package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.collection.chat.Group;

import java.util.List;

public interface ChatPort {
  List<String> getUserIdsFromChannel(String channelId);

  Group createGroup(Group newGroup);

  Group getGroup(String groupId);

  boolean isUserInGroup(Long userId);

  boolean isExistGroup(String groupId);
}
