package com.GHTK.Social_Network.application.port.output.chat;

import com.GHTK.Social_Network.domain.collection.EStateUserGroup;
import com.GHTK.Social_Network.domain.collection.UserCollectionDomain;
import com.GHTK.Social_Network.domain.collection.chat.Group;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;

import java.util.List;
import java.util.Optional;

public interface GroupPort {
  UserCollectionDomain saveUser(UserCollectionDomain user);

  Group saveGroup(Group newGroup);

  Group createGroupPersonal(Long userSendId, Long userReceiveId);

  Group getGroupForPersonal(String groupName);

  Group getGroupForGroup(String groupId);

  boolean isUserInGroup(Long userId, String groupId);

  List<Group> getPageMyGroups(Long userId, PaginationRequest paginationRequest);

  UserCollectionDomain findUserListGroupWithPagination(Long userId, int skip, int limit);

//  void outGroup(String groupId);

  Group createGroupGroup(Long userSendId, List<Long> userReceiveIds);

  Group setMemberRole(Long userId, String groupId, EStateUserGroup newRole);

  Group updateGroup(String groupId, Group updateGroup);

  void deleteGroup(String groupId);
}
