package com.GHTK.Social_Network.application.port.output.chat;

import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.EMemberRole;

import java.util.List;

public interface GroupPort {
  Group createGroup(Group newGroup);

  Group createGroupPersonal(Long userSendId, Long userReceiveId);

  Group getGroup(String groupId);

  boolean isUserInGroup(Long userId, String groupId);

  Group createGroupGroup(Long userSendId, List<Long> userReceiveIds);

  Group setMemberRole(Long userId, String groupId, EMemberRole newRole);

  Group updateGroup(String groupId, Group updateGroup);

  void deleteGroup(String groupId);
}
