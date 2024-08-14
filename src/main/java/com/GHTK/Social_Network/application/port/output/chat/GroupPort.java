package com.GHTK.Social_Network.application.port.output.chat;

import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.collection.chat.Group;

public interface GroupPort {
  UserCollectionDomain saveUser(UserCollectionDomain user);

  Group saveGroup(Group newGroup);

  Group createGroupPersonal(Long userSendId, Long userReceiveId);

  Group getGroupForPersonal(String groupName);

  Group getGroupForGroup(String groupId);

  boolean isUserInGroup(Long userId, String groupId);
}
