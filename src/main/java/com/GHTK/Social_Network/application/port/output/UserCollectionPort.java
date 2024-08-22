package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.collection.UserGroup;

public interface UserCollectionPort {
  UserGroup getUserGroupByUserId(Long userId, String groupId);

  void addUserGroup(Long userId, UserGroup newUserGroup);

  void removeUserGroup(Long userId, String groupId);
}
