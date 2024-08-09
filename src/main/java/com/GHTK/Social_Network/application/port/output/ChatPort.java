package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.collection.chat.Group;

public interface ChatPort {
  Group createGroup(Group newGroup);

  Group createGroupPersonal(Long userSendId, Long userReceiveId);

  Group getGroup(String groupId);

  boolean isUserInGroup(Long userId, String groupId);
}
