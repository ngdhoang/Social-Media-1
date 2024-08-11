package com.GHTK.Social_Network.application.port.output.chat;

import com.GHTK.Social_Network.domain.collection.chat.Group;

public interface GroupPort {
  Group createGroup(Group newGroup);

  Group createGroupPersonal(Long userSendId, Long userReceiveId);

  Group getGroup(String groupId);

  boolean isUserInGroup(Long userId, String groupId);
}
